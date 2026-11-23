package org.pinzones;

import java.nio.file.Path;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

public class FitxersJSON {

    public static void main(String[] args) throws java.io.IOException {

        Path dades = Path.of("conversor_dades", "app", "dades");

        ArrayList<Path> RutaJson = new ArrayList<>();


        try (Stream<Path> entrades = Files.walk(dades)) {
            
            entrades
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(RutaJson::add);
        }

        for (int i = 0; i < RutaJson.size(); i++) {

            System.out.println("\n\n\n\n\n" + RutaJson.get(i) + " =================================");

            try (BufferedReader lector = Files.newBufferedReader(RutaJson.get(i), StandardCharsets.UTF_8)) {
                String linia;
                while ((linia = lector.readLine()) != null) {
                    System.out.println(linia);
                }
            }
        
        }
    
    }
}
