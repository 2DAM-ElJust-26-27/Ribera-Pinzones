package org.pinzones;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.stream.Stream;

public class FitxersXML {

    public static void main(String[] args) throws java.io.IOException {

        Path dades = Path.of("conversor_dades", "app", "dades");

        try (Stream<Path> entrades = Files.walk(dades)) {
            entrades
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".xml"))
                    .forEach(System.out::println);
        }
    }
    
}
