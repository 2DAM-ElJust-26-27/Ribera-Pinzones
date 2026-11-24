package org.pinzones;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class ImportadorCentres {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void importar(Path archivo) throws IOException, SQLException {

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try {

                JsonNode centros = mapper.readTree(archivo.toFile());

                // Obtener importación
                long importacioId;

                try (PreparedStatement ps = con.prepareStatement("""
                        INSERT INTO importacio (clau, versio)
                        VALUES ('centres', 1)
                        ON CONFLICT (clau) DO UPDATE
                        SET versio = EXCLUDED.versio
                        RETURNING id
                        """)) {

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    importacioId = rs.getLong("id");
                }

                int contador = 0;

                for (JsonNode centro : centros) {

                    String codi = texto(centro, "codi");

                    // Comprobar si ya está importado
                    try (PreparedStatement ps = con.prepareStatement("""
                            SELECT 1 FROM registre_importacio
                            WHERE importacio_id = ?
                            AND tipus_registre = 'centre'
                            AND codi_origen = ?
                            """)) {

                        ps.setLong(1, importacioId);
                        ps.setString(2, codi);

                        if (ps.executeQuery().next()) {
                            continue;
                        }
                    }

                    // Resolver relaciones
                    Long tipus = obtenerId(con, "tipus_centre",
                            texto(centro, "tipus_centre_codi"));

                    Long titular = obtenerId(con, "entitat",
                            texto(centro, "titular_codi"));

                    Long gestor = obtenerId(con, "entitat",
                            texto(centro, "gestor_codi"));

                    Long municipi = obtenerId(con, "municipi",
                            texto(centro, "municipi_codi"));

                    // Insertar centro
                    long centreId;

                    try (PreparedStatement ps = con.prepareStatement("""
                            INSERT INTO centre (
                                nom, tipus_centre_id, titular_id, gestor_id,
                                municipi_id, adreca, codi_postal,
                                latitud, longitud, precisio_punt,
                                imatge, font_codi, adreca_font_codi
                            )
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            RETURNING id
                            """)) {

                        ps.setString(1, texto(centro, "nom"));
                        ponerLong(ps, 2, tipus);
                        ponerLong(ps, 3, titular);
                        ponerLong(ps, 4, gestor);
                        ponerLong(ps, 5, municipi);

                        ps.setString(6, texto(centro, "adreca"));
                        ps.setString(7, texto(centro, "codi_postal"));

                        ps.setObject(8, centro.path("latitud").isNull()
                                ? null : centro.get("latitud").decimalValue());

                        ps.setObject(9, centro.path("longitud").isNull()
                                ? null : centro.get("longitud").decimalValue());

                        ps.setString(10, texto(centro, "precisio_punt"));
                        ps.setString(11, texto(centro, "imatge"));
                        ps.setString(12, texto(centro, "font_codi"));
                        ps.setString(13, texto(centro, "adreca_font_codi"));

                        ResultSet rs = ps.executeQuery();
                        rs.next();
                        centreId = rs.getLong("id");
                    }

                    // Registrar código de origen
                    registrar(con, importacioId, "centre", codi, centreId);

                    // Importar contactos
                    for (JsonNode contacto : centro.path("contactes")) {

                        insertar(con, """
                                INSERT INTO contacte
                                (centre_id, tipus, valor, font_codi)
                                VALUES (?, ?, ?, ?)
                                """, centreId,
                                texto(contacto, "tipus"),
                                texto(contacto, "valor"),
                                texto(contacto, "font_codi"));
                    }

                    // Importar horarios
                    for (JsonNode horario : centro.path("horaris")) {

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
                                (centre_id, observacions, font_codi)
                                VALUES (?, ?, ?)
                                """, centreId, observaciones,
                                texto(horario, "font_codi"));
                    }

                    contador++;
                }

                con.commit();

                System.out.println("Centros importados: " + contador);

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

        if (codigo == null) return null;

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


    private static String texto(JsonNode nodo, String campo) {

        JsonNode valor = nodo.get(campo);

        return valor == null || valor.isNull()
                ? null : valor.asText();
    }


    private static void ponerLong(
            PreparedStatement ps, int posicion, Long valor)
            throws SQLException {

        if (valor == null) {
            ps.setNull(posicion, Types.BIGINT);
        } else {
            ps.setLong(posicion, valor);
        }
    }
}