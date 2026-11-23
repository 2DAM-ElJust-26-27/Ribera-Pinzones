package org.pinzones;

import java.io.IOException;
import java.nio.file.Path;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public final class FitxersJSON {

    public static void leer(Path archivo) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        // Leer el archivo JSON
        JsonNode raiz = mapper.readTree(archivo.toFile());

        System.out.println("\nArchivo: " + archivo.getFileName());

        // Comprobar si el JSON contiene una lista
        if (raiz.isArray()) {

            System.out.println("Elementos encontrados: " + raiz.size());

            // Recorrer cada objeto de la lista
            for (JsonNode objeto : raiz) {

                System.out.println("\n----------------------");

                for (var campo : objeto.properties()) {

                    String nombre = campo.getKey();
                    JsonNode valor = campo.getValue();

                    System.out.println(nombre + ": " + valor);
                }
            }

        } else {
            System.out.println("El JSON no contiene una lista.");
        }
    }
}
