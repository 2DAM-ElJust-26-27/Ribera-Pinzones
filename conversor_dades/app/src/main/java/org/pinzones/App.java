package org.pinzones;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) {

        Path carpeta = Path.of("dades");

        // Comprobar que existe la carpeta
        if (!Files.isDirectory(carpeta)) {
            System.out.println("No se encuentra la carpeta: "
                    + carpeta.toAbsolutePath());
            return;
        }

        // Obtener los archivos de la carpeta y procesarlos
        try (Stream<Path> archivos = Files.list(carpeta)) {

            archivos
                    .filter(Files::isRegularFile)
                    .sorted()
                    .forEach(App::procesarArchivo);

        } catch (IOException e) {
            System.out.println("Error al acceder a la carpeta: "
                    + e.getMessage());
        }
    }

    private static void procesarArchivo(Path archivo) {

    String archivoCompleto = archivo.getFileName().toString();
    String nombreArchivo = obtenerNombreA(archivoCompleto);

    try {
        switch (nombreArchivo) {

            case "catalegs":
                ImportadorCatalegs.importar(archivo);
                break;

            case "centres":
                ImportadorCentres.importar(archivo);
                break;

            case "entitats":
                ImportadorEntitats.importar(archivo);
                break;

            case "fonts":
                ImportadorFonts.importar(archivo);
                break;

            case "serveis":
                ImportadorServeis.importar(archivo);
                break;

            case "territori":
                ImportadorTerritori.importar(archivo);
                break;

            default:
                System.out.println("Formato no reconocido: " + nombreArchivo);
                break;
        }

    } catch (IOException | SQLException e) {
        System.out.println("Error procesando "
                + archivoCompleto + ": " + e.getMessage());
    }
}


    // Obtener el nombre del archivo sin extensión
    private static String obtenerNombreA(String archivo) {

        int divisor = archivo.lastIndexOf('.');

        if (divisor != -1) {
            return archivo.substring(0, divisor).toLowerCase();
        }

        return archivo.toLowerCase();
    }

    // Obtener la extensión del archivo
    private static String obtenerExtensionA(String archivo) {

        int divisor = archivo.lastIndexOf('.');

        if (divisor != -1) {
            return archivo.substring(divisor + 1).toLowerCase();
        }

        return "";
    }
}

