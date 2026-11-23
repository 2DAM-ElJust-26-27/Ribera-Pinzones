package org.pinzones;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) {

        // Ruta a la carpeta de datos
        Path carpeta = Path.of("dades");

        // Comprobar que existe la carpeta
        if (!Files.isDirectory(carpeta)) {
            System.out.println("No se encuentra la carpeta: "
                    + carpeta.toAbsolutePath());
            return;
        }

        // Obtener los archivos de la carpeta
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

    // Identificar y procesar cada archivo
    private static void procesarArchivo(Path archivo) {

        String nombre = archivo.getFileName().toString();

        int posicion = nombre.lastIndexOf('.');
        String extension = "";

        if (posicion != -1) {
            extension = nombre.substring(posicion + 1).toLowerCase();
        }

        try {
            switch (extension) {

                case "csv":
                    FitxersCSV.leer(archivo);
                    break;

                case "json":
                    FitxersJSON.leer(archivo);
                    break;

                case "xml":
                    FitxersXML.leer(archivo);
                    break;

                default:
                    System.out.println("Formato no reconocido: " + nombre);
                    break;
            }

        } catch (IOException e) {
            System.out.println("Error procesando " + nombre + ": "
                    + e.getMessage());
        }
    }
}

