
package org.pinzones;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public final class FitxersCSV {

    public static void leer(Path archivo) throws IOException {

        try (Reader reader = Files.newBufferedReader(
                    archivo, StandardCharsets.UTF_8);

             CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(reader)) {

            // Mostrar los encabezados
            System.out.println("\nArchivo: " + archivo.getFileName());
            System.out.println("Columnas: " + parser.getHeaderNames());

            // Recorrer los registros
            for (CSVRecord registro : parser) {

                for (String columna : parser.getHeaderNames()) {
                    System.out.print(
                            columna + ": " + registro.get(columna) + " | "
                    );
                }

                System.out.println();
            }
        }
    }
}
