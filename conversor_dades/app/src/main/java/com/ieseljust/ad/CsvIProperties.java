package com.ieseljust.ad;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/** Exemples de l'apartat 6: CSV i fitxers de propietats. */
public final class CsvIProperties {

    private static final Path DADES = Path.of("dades");
    private static final Path CONFIG = Path.of("config");

    /** Llig un CSV amb capçalera i respecta camps entre cometes. */
    public static void llegirCsv() throws IOException {
        Path ruta = DADES.resolve("productes.csv");
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .get()
                     .parse(reader)) {

            for (CSVRecord registre : parser) {
                int id = Integer.parseInt(registre.get("id"));
                String nom = registre.get("nom");
                BigDecimal preu = new BigDecimal(registre.get("preu"));
                System.out.printf("%d: %s (%s €)%n", id, nom, preu);
            }
        }
    }

    /** CSVPrinter aplica automàticament les regles d'escapat del format. */
    public static void escriureCsv() throws IOException {
        Path ruta = DADES.resolve("productes_generats.csv");
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8);
             CSVPrinter csv = new CSVPrinter(
                     writer,
                     CSVFormat.DEFAULT.builder()
                             .setHeader("id", "nom", "preu")
                             .get())) {
            csv.printRecord(1, "Ratolí, sense fil", "19.95");
            csv.printRecord(2, "Teclat", "29.95");
        }
        System.out.println("CSV generat: " + ruta);
    }

    /** Properties carrega parelles textuals clau=valor. */
    public static void llegirProperties() throws IOException {
        Properties configuracio = carregarProperties(
                CONFIG.resolve("aplicacio.properties"));

        String host = configuracio.getProperty("db.host", "localhost");
        int port = Integer.parseInt(
                configuracio.getProperty("db.port", "5432"));
        String baseDades = configuracio.getProperty("db.name", "botiga");

        System.out.printf("Base de dades: %s:%d/%s%n", host, port, baseDades);
    }

    /** Guarda una configuració textual; no s'hi han d'incloure secrets. */
    public static void escriureProperties() throws IOException {
        Properties configuracio = new Properties();
        configuracio.setProperty("app.idioma", "ca");
        configuracio.setProperty("app.tema", "clar");

        Path ruta = CONFIG.resolve("aplicacio_generada.properties");
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            configuracio.store(writer, "Configuració de l'aplicació");
        }
        System.out.println("Configuració generada: " + ruta);
    }

    /**
     * Activitat: la configuració determina rutes, delimitador i codificació.
     * Les files vàlides van a JSON i les descartades a un informe.
     */
    public static void activitatCsvAJson() throws IOException {
        Properties config = carregarProperties(CONFIG.resolve("importacio.properties"));
        Path entrada = Path.of(config.getProperty("entrada"));
        Path eixida = Path.of(config.getProperty("eixida"));
        Path informe = Path.of(config.getProperty("informe"));
        Charset charset = Charset.forName(config.getProperty("codificacio", "UTF-8"));
        char delimitador = llegirDelimitador(config.getProperty("delimitador", ","));

        List<ProducteCsv> valids = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(entrada, charset);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setDelimiter(delimitador)
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .get()
                     .parse(reader);
             BufferedWriter errors = Files.newBufferedWriter(informe, charset)) {

            for (CSVRecord registre : parser) {
                try {
                    int id = Integer.parseInt(registre.get("id"));
                    String nom = registre.get("nom").trim();
                    BigDecimal preu = new BigDecimal(registre.get("preu"));

                    if (id <= 0 || nom.isBlank() || preu.signum() < 0) {
                        throw new IllegalArgumentException("valors fora de rang");
                    }
                    valids.add(new ProducteCsv(id, nom, preu));
                } catch (RuntimeException e) {
                    errors.write("Fila " + registre.getRecordNumber()
                            + ": " + e.getMessage());
                    errors.newLine();
                }
            }
        }

        // En Jackson 3, ObjectMapper és immutable i es configura amb un builder.
        ObjectMapper mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        mapper.writeValue(eixida.toFile(), valids);
        System.out.printf("%d registres exportats a %s.%n", valids.size(), eixida);
        System.out.println("Files descartades: " + informe);
    }

    private static Properties carregarProperties(Path ruta) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private static char llegirDelimitador(String valor) {
        if (valor.length() != 1) {
            throw new IllegalArgumentException(
                    "El delimitador ha de tindre exactament un caràcter");
        }
        return valor.charAt(0);
    }

    public record ProducteCsv(int id, String nom, BigDecimal preu) {
    }
}
