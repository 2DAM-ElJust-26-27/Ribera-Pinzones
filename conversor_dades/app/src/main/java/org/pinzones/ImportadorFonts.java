
package org.pinzones;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class ImportadorFonts {

    public static void importar(Path archivo) throws IOException, SQLException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode datos = mapper.readTree(archivo.toFile());
        JsonNode fonts = datos.get("fonts");

        try (Connection con = Conexion.conectar()) {

            con.setAutoCommit(false);

            try {

                for (JsonNode font : fonts) {

                    String codi = font.get("codi").asString();
                    String referencia = font.get("referencia").asString();

                    try (PreparedStatement ps = con.prepareStatement("""
                            INSERT INTO font (codi, referencia)
                            VALUES (?, ?)
                            ON CONFLICT (codi) DO NOTHING
                            """)) {

                        ps.setString(1, codi);
                        ps.setString(2, referencia);

                        ps.executeUpdate();
                    }
                }

                con.commit();

                System.out.println("Fuentes importadas: " + fonts.size());

            } catch (SQLException e) {

                con.rollback();
                throw e;
            }
        }
    }
}
