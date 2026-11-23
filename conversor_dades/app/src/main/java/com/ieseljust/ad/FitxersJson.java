package com.ieseljust.ad;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Exemples de l'apartat 5: mapatge i arbre JSON amb Jackson. */
public final class FitxersJson {

    private static final Path DADES = Path.of("dades");

    private static ObjectMapper mapper() {
        // En Jackson 3, ObjectMapper és immutable i es configura amb un builder.
        return JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
    }

    /** Converteix un objecte JSON en un record Java. */
    public static void llegirObjecte() throws IOException {
        Producte producte = mapper().readValue(
                DADES.resolve("producte.json").toFile(), Producte.class);
        validar(producte);
        System.out.println("Producte: " + producte);
    }

    /** TypeReference conserva el tipus Producte de la llista en temps d'execució. */
    public static void llegirLlista() throws IOException {
        List<Producte> productes = mapper().readValue(
                DADES.resolve("productes.json").toFile(),
                new TypeReference<List<Producte>>() {
                });

        for (Producte producte : productes) {
            validar(producte);
            System.out.println(producte.nom());
        }
    }

    /** Serialitza objectes Java com un JSON formatat. */
    public static void escriureObjectes() throws IOException {
        List<Producte> productes = List.of(
                new Producte(1, "Ratolí", 19.95, List.of("usb"), true),
                new Producte(2, "Teclat", 29.95, List.of("usb", "perifèric"), true));

        Path ruta = DADES.resolve("productes_generats.json");
        mapper().writeValue(ruta.toFile(), productes);
        System.out.println("JSON generat: " + ruta);
    }

    /** L'arbre JsonNode permet inspeccionar un document sense un model Java. */
    public static void llegirArbre() throws IOException {
        JsonNode arrel = mapper().readTree(DADES.resolve("producte.json").toFile());
        String nom = arrel.path("nom").asString();
        double preu = arrel.path("preu").asDouble();
        System.out.printf("%s costa %.2f €%n", nom, preu);
    }

    /**
     * Activitat: valida llibres, informa dels incorrectes, ordena els vàlids
     * per autor i títol i genera un JSON nou.
     */
    public static void activitatLlibres() throws IOException {
        ObjectMapper mapper = mapper();
        List<Llibre> entrada = mapper.readValue(
                DADES.resolve("llibres.json").toFile(),
                new TypeReference<List<Llibre>>() {
                });

        List<Llibre> valids = new ArrayList<>();
        for (int i = 0; i < entrada.size(); i++) {
            Llibre llibre = entrada.get(i);
            String error = errorValidacio(llibre);
            if (error == null) {
                valids.add(llibre);
            } else {
                System.err.printf("Llibre %d descartat: %s%n", i + 1, error);
            }
        }

        valids.sort(Comparator
                .comparing(Llibre::autor, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Llibre::titol, String.CASE_INSENSITIVE_ORDER));

        Path destinacio = DADES.resolve("llibres_ordenats.json");
        mapper.writeValue(destinacio.toFile(), valids);
        System.out.printf("S'han escrit %d llibres en %s.%n", valids.size(), destinacio);
    }

    private static void validar(Producte producte) {
        if (producte == null || producte.id() <= 0
                || producte.nom() == null || producte.nom().isBlank()
                || producte.preu() < 0) {
            throw new IllegalArgumentException("Producte no vàlid");
        }
    }

    private static String errorValidacio(Llibre llibre) {
        if (llibre == null) {
            return "registre nul";
        }
        if (llibre.id() <= 0) {
            return "l'identificador ha de ser positiu";
        }
        if (llibre.autor() == null || llibre.autor().isBlank()) {
            return "falta l'autor";
        }
        if (llibre.titol() == null || llibre.titol().isBlank()) {
            return "falta el títol";
        }
        return null;
    }

    public record Producte(
            int id, String nom, double preu,
            List<String> etiquetes, boolean disponible) {
    }

    public record Llibre(int id, String autor, String titol) {
    }
}
