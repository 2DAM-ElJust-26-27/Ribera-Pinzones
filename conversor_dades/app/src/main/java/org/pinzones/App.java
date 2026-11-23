package org.pinzones;

import java.nio.file.Path;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) throws java.io.IOException {

        Path ruta = Path.of("conversor_dades", "app", "dades", "catalegs.csv");

        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linia;
            while ((linia = lector.readLine()) != null) {
                System.out.println(linia);
            }
        }
    }
}
