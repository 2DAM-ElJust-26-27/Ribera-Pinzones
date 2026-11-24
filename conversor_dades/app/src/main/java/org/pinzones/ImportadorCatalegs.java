package org.pinzones;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImportadorCatalegs {

    public static void importar(Path archivo) throws IOException, SQLException {

        // Consultas SQL
        String sqlEntitat = """
                INSERT INTO tipus_entitat (nom)
                VALUES (?)
                ON CONFLICT (nom) DO NOTHING
                """;

        String sqlCentre = """
                INSERT INTO tipus_centre (nom)
                VALUES (?)
                ON CONFLICT (nom) DO NOTHING
                """;

        String sqlCollectiu = """
                INSERT INTO collectiu (nom)
                VALUES (?)
                ON CONFLICT (nom) DO NOTHING
                """;

        // Conectar con PostgreSQL
        try (Connection conexion = Conexion.conectar();

             PreparedStatement insertarEntitat =
                     conexion.prepareStatement(sqlEntitat);

             PreparedStatement insertarCentre =
                     conexion.prepareStatement(sqlCentre);

             PreparedStatement insertarCollectiu =
                     conexion.prepareStatement(sqlCollectiu);

             BufferedReader lector = Files.newBufferedReader(
                     archivo, StandardCharsets.UTF_8)) {

            // Desactivar autocommit para controlar la transacción
            conexion.setAutoCommit(false);

            String linea;

            // Saltar la cabecera
            lector.readLine();

            int contador = 0;

            try {

                // Leer el CSV línea por línea
                while ((linea = lector.readLine()) != null) {

                    String[] datos = linea.split(",", 3);

                    if (datos.length != 3) {
                        System.out.println(
                                "Línea incorrecta: " + linea);
                        continue;
                    }

                    String tipus = datos[0].trim();
                    String codi = datos[1].trim();
                    String nom = datos[2].trim();

                    // Identificar la tabla correspondiente
                    switch (tipus) {

                        case "forma_juridica":

                            insertarEntitat.setString(1, nom);
                            insertarEntitat.executeUpdate();
                            break;

                        case "tipus_centre":

                            insertarCentre.setString(1, nom);
                            insertarCentre.executeUpdate();
                            break;

                        case "collectiu":

                            insertarCollectiu.setString(1, nom);
                            insertarCollectiu.executeUpdate();
                            break;

                        default:

                            System.out.println("Tipo no reconocido: " + tipus);
                            break;
                    }

                    contador++;
                }

                // Confirmar los cambios
                conexion.commit();

                System.out.println(
                        "Importación de catalegs completada.");

                System.out.println(
                        "Registros procesados: " + contador);

            } catch (SQLException | IOException e) {

                // Deshacer cambios si ocurre un error
                conexion.rollback();

                throw e;
            }
        }
    }
}
