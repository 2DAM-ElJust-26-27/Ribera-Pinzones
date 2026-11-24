
package org.pinzones;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;

public class ImportadorTerritori {

    public static void importar(Path archivo) throws IOException, SQLException {

        List<String> lineas = Files.readAllLines(archivo);

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try {

                // Crear o recuperar la importacion
                long importacionId;

                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO importacio(clau, versio) VALUES('territori', 1) "
                        + "ON CONFLICT(clau) DO UPDATE SET versio = EXCLUDED.versio "
                        + "RETURNING id")) {

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    importacionId = rs.getLong("id");
                }

                // Recorrer las lineas del CSV (la primera es la cabecera)
                for (int i = 1; i < lineas.size(); i++) {

                    String[] datos = lineas.get(i).split(",", -1);

                    String codiMunicipi = datos[0].trim();
                    String nomMunicipi = datos[1].trim();
                    String codiComarca = datos[2].trim();
                    String nomComarca = datos[3].trim();
                    String fontCodi = datos[4].trim();

                    // 1. Insertar comarca si no existe
                    long comarcaId;

                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO comarca(nom) VALUES(?) "
                            + "ON CONFLICT(nom) DO NOTHING")) {

                        ps.setString(1, nomComarca);
                        ps.executeUpdate();
                    }

                    // Obtener ID de la comarca
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT id FROM comarca WHERE nom = ?")) {

                        ps.setString(1, nomComarca);
                        ResultSet rs = ps.executeQuery();
                        rs.next();
                        comarcaId = rs.getLong("id");
                    }

                    // Registrar codigo de comarca
                    registrar(con, importacionId, "comarca",
                            codiComarca, comarcaId);

                    // 2. Insertar municipio si no existe
                    long municipiId;

                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO municipi(nom, comarca_id) VALUES(?, ?) "
                            + "ON CONFLICT(nom) DO NOTHING")) {

                        ps.setString(1, nomMunicipi);
                        ps.setLong(2, comarcaId);
                        ps.executeUpdate();
                    }

                    // Obtener ID del municipio
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT id FROM municipi WHERE nom = ?")) {

                        ps.setString(1, nomMunicipi);
                        ResultSet rs = ps.executeQuery();
                        rs.next();
                        municipiId = rs.getLong("id");
                    }

                    // Registrar codigo de municipio
                    registrar(con, importacionId, "municipi",
                            codiMunicipi, municipiId);

                }

                con.commit();
                System.out.println("Territori importado correctamente.");

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private static void registrar(Connection con, long importacionId,
            String tipo, String codi, long idPostgres) throws SQLException {

        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO registre_importacio "
                + "(importacio_id, tipo_registre, codi_origen, id_postgres) "
                + "VALUES (?, ?, ?, ?) "
                + "ON CONFLICT(importacio_id, tipo_registre, codi_origen) "
                + "DO UPDATE SET id_postgres = EXCLUDED.id_postgres")) {

            ps.setLong(1, importacionId);
            ps.setString(2, tipo);
            ps.setString(3, codi);
            ps.setLong(4, idPostgres);

            ps.executeUpdate();
        }
    }
}
