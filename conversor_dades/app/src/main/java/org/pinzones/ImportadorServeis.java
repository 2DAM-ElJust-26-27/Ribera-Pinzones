
package org.pinzones;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class ImportadorServeis {

    public static void importar(Path archivo) throws IOException, SQLException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode serveis = mapper.readTree(archivo.toFile());

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try {

                // Obtener importación
                long importacioId;

                try (PreparedStatement ps = con.prepareStatement("""
                        INSERT INTO importacio (clau, versio)
                        VALUES ('serveis', 1)
                        ON CONFLICT (clau) DO UPDATE
                        SET versio = EXCLUDED.versio
                        RETURNING id
                        """)) {

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    importacioId = rs.getLong("id");
                }

                int contador = 0;

                for (JsonNode servei : serveis) {

                    String codi = texto(servei, "codi");

                    // Comprobar si ya está importado
                    try (PreparedStatement ps = con.prepareStatement("""
                            SELECT 1 FROM registre_importacio
                            WHERE importacio_id = ?
                            AND tipus_registre = 'servei'
                            AND codi_origen = ?
                            """)) {

                        ps.setLong(1, importacioId);
                        ps.setString(2, codi);

                        if (ps.executeQuery().next()) {
                            continue;
                        }
                    }

                    // Insertar servicio
                    long serveiId;

                    try (PreparedStatement ps = con.prepareStatement("""
                            INSERT INTO servei (
                                nom, descripcio, via_acces,
                                imatge, observacions, font_codi
                            )
                            VALUES (?, ?, ?, ?, ?, ?)
                            RETURNING id
                            """)) {

                        ps.setString(1, texto(servei, "nom"));
                        ps.setString(2, texto(servei, "descripcio"));
                        ps.setString(3, texto(servei, "via_acces"));
                        ps.setString(4, texto(servei, "imatge"));
                        ps.setString(5, texto(servei, "observacions"));
                        ps.setString(6, texto(servei, "font_codi"));

                        ResultSet rs = ps.executeQuery();
                        rs.next();
                        serveiId = rs.getLong("id");
                    }

                    // Registrar código de origen
                    registrar(con, importacioId, "servei", codi, serveiId);

                    // Importar colectivos
                    for (JsonNode colectivo : servei.path("collectius")) {

                        Long colectivoId = obtenerId(con,
                            "collectiu", colectivo.asString());

                        if (colectivoId != null) {

                            insertar(con, """
                                    INSERT INTO servei_collectiu
                                    (servei_id, collectiu_id)
                                    VALUES (?, ?)
                                    ON CONFLICT DO NOTHING
                                    """, serveiId, colectivoId);
                        }
                    }

                    // Importar horarios
                    for (JsonNode horario : servei.path("horaris")) {

                        String observaciones =
                                "Tipo: " + texto(horario, "tipus")
                                + " | Descripción: "
                                + texto(horario, "descripcio")
                                + " | Zona: "
                                + texto(horario, "zona_horaria")
                                + " | Desde: "
                                + texto(horario, "vigent_des_de")
                                + " | Hasta: "
                                + texto(horario, "vigent_fins_a");

                        insertar(con, """
                                INSERT INTO horari
                                (servei_id, observacions, font_codi)
                                VALUES (?, ?, ?)
                                """, serveiId, observaciones,
                                texto(horario, "font_codi"));
                    }

                    // Importar prestaciones
                    for (JsonNode prestacio : servei.path("prestacions")) {

                        Long entitatId = obtenerId(con,
                                "entitat",
                                texto(prestacio, "entitat_codi"));

                        if (entitatId == null) {
                            System.out.println("Entidad no encontrada: "
                                    + texto(prestacio, "entitat_codi"));
                            continue;
                        }

                        // Insertar prestación
                        long prestacioId;

                        try (PreparedStatement ps = con.prepareStatement("""
                                INSERT INTO prestacio
                                (servei_id, entitat_id, font_codi)
                                VALUES (?, ?, ?)
                                RETURNING id
                                """)) {

                            ps.setLong(1, serveiId);
                            ps.setLong(2, entitatId);
                            ps.setString(3, texto(prestacio, "font_codi"));

                            ResultSet rs = ps.executeQuery();
                            rs.next();
                            prestacioId = rs.getLong("id");
                        }

                        // Relacionar prestación con centros
                        for (JsonNode centre : prestacio.path("centres")) {

                            Long centreId = obtenerId(con,
                                    "centre",
                                    texto(centre, "centre_codi"));

                            if (centreId != null) {

                                insertar(con, """
                                        INSERT INTO prestacio_centre
                                        (prestacio_id, centre_id, font_codi)
                                        VALUES (?, ?, ?)
                                        ON CONFLICT DO NOTHING
                                        """, prestacioId, centreId,
                                        texto(centre, "font_codi"));
                            }
                        }
                    }

                    contador++;
                }

                con.commit();

                System.out.println("Servicios importados: " + contador);

            } catch (SQLException e) {

                con.rollback();
                throw e;
            }
        }
    }


    // Buscar ID mediante código de origen
    private static Long obtenerId(
            Connection con, String tipo, String codigo)
            throws SQLException {

        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        try (PreparedStatement ps = con.prepareStatement("""
                SELECT id_postgres
                FROM registre_importacio ri
                JOIN importacio i ON i.id = ri.importacio_id
                WHERE ri.tipus_registre = ?
                AND ri.codi_origen = ?
                ORDER BY i.data_importacio DESC
                LIMIT 1
                """)) {

            ps.setString(1, tipo);
            ps.setString(2, codigo);

            ResultSet rs = ps.executeQuery();

            return rs.next() ? rs.getLong(1) : null;
        }
    }


    // Registrar código e ID
    private static void registrar(
            Connection con, long importacioId,
            String tipo, String codigo, long id)
            throws SQLException {

        insertar(con, """
                INSERT INTO registre_importacio
                (importacio_id, tipus_registre, codi_origen, id_postgres)
                VALUES (?, ?, ?, ?)
                """, importacioId, tipo, codigo, id);
    }


    // Ejecutar inserciones sencillas
    private static void insertar(
            Connection con, String sql, Object... valores)
            throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < valores.length; i++) {
                ps.setObject(i + 1, valores[i]);
            }

            ps.executeUpdate();
        }
    }


    // Obtener texto del JSON
    private static String texto(JsonNode nodo, String campo) {

        JsonNode valor = nodo.get(campo);

        return valor == null || valor.isNull()
                ? null : valor.asString();
    }
}
