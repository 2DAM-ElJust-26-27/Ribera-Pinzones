package com.ieseljust.ad;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Exemples de l'apartat 3: dades binàries i objectes. */
public final class DadesBinaries {

    private static final Path DADES = Path.of("dades");
    private static final String CAPCALERA = "AD26";
    private static final int VERSIO = 1;

    /** Escriu tipus primitius i els recupera en el mateix ordre. */
    public static void tipusPrimitius() throws IOException {
        Path ruta = DADES.resolve("producte.dat");

        try (DataOutputStream eixida = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(ruta)))) {
            eixida.writeInt(42);
            eixida.writeUTF("Teclat");
            eixida.writeDouble(29.95);
        }

        try (DataInputStream entrada = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(ruta)))) {
            int id = entrada.readInt();
            String nom = entrada.readUTF();
            double preu = entrada.readDouble();
            System.out.printf("%d: %s (%.2f €)%n", id, nom, preu);
        }
    }

    /** Modifica el tercer enter del fitxer sense llegir els dos anteriors. */
    public static void accesAleatori() throws IOException {
        Path ruta = DADES.resolve("comptadors.dat");

        try (DataOutputStream eixida = new DataOutputStream(Files.newOutputStream(ruta))) {
            eixida.writeInt(10);
            eixida.writeInt(20);
            eixida.writeInt(30);
        }

        try (RandomAccessFile fitxer = new RandomAccessFile(ruta.toFile(), "rw")) {
            long posicioTercer = 2L * Integer.BYTES; // 0: 1a posició; 1: 2a posició; 2: 3a posició
            // seek necessita un int com a paràmetre (podem referenciar fins quasi 2 GB)
            // Per això multipliquem per 2L en lloc de 2
            fitxer.seek(posicioTercer);
            int valor = fitxer.readInt();

            fitxer.seek(posicioTercer);
            fitxer.writeInt(valor + 1);
            System.out.println("Tercer comptador: " + (valor + 1));
        }
    }

    /** Serialitza i recupera un objecte Java controlat per l'aplicació. */
    public static void serialitzacioObjectes()
            throws IOException, ClassNotFoundException {
        Path ruta = DADES.resolve("producte.ser");
        Producte original = new Producte(42, "Teclat", 29.95);

        // Escriptura de l'objecte
        try (ObjectOutputStream eixida = new ObjectOutputStream(Files.newOutputStream(ruta))) {
            eixida.writeObject(original);
        }

        // Lectura de l'objecte
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(ruta))) {
            Producte recuperat = (Producte) entrada.readObject();
            System.out.println("Objecte recuperat: " + recuperat);
        }
    }

    /**
     * Activitat: format binari amb capçalera, versió, nombre d'elements
     * i temperatures. L'ordre i els tipus constitueixen el contracte.
     */
    public static void activitatTemperatures() throws IOException {
        // Definim la ruta al fitxer de temperatures
        Path ruta = DADES.resolve("temperatures.dat");

        // Escrivim la llista de temperatures al fitxer
        escriureTemperatures(ruta, List.of(18.5, 20.0, 21.75));

        // Recuperem les temperatures del fitxer i les mostrem
        System.out.println("Temperatures recuperades: " + llegirTemperatures(ruta));
    }

    private static void escriureTemperatures(Path ruta, List<Double> temperatures)
            throws IOException {
        try (DataOutputStream eixida = new DataOutputStream(Files.newOutputStream(ruta))) {
            // En primer lloc escrivim la capçalera, la versió i la longitud del fitxer (quantitat de temperatures)
            eixida.writeUTF(CAPCALERA);
            eixida.writeInt(VERSIO);
            eixida.writeInt(temperatures.size());
            for (double temperatura : temperatures) {
                eixida.writeDouble(temperatura);
            }
        }
    }

    private static List<Double> llegirTemperatures(Path ruta) throws IOException {
        try (DataInputStream entrada = new DataInputStream(Files.newInputStream(ruta))) {
            // Llegim seqüencialment la capçalera i la versió, per comparar si és correcte
            String capcalera = entrada.readUTF();
            int versio = entrada.readInt();

            if (!CAPCALERA.equals(capcalera)) {
                throw new IOException("Capçalera incorrecta: " + capcalera);
            }
            if (versio != VERSIO) {
                throw new IOException("Versió no compatible: " + versio);
            }

            // Si la capálera i versió són correctes, llegim les temperatures
            int quantitat = entrada.readInt();
            if (quantitat < 0 || quantitat > 10_000) { // limitem a 10.000 la quantitat
                throw new IOException("Quantitat de temperatures no vàlida");
            }

            var temperatures = new java.util.ArrayList<Double>(quantitat);
            for (int i = 0; i < quantitat; i++) {
                temperatures.add(entrada.readDouble());
            }
            return temperatures;
        }
    }

    public record Producte(int id, String nom, double preu)
            implements Serializable {
        // serialVersionUID és una propietat heretada de serializable
        // Internament l'utilitza ObjectOutputStream i ObjectInputStream
        private static final long serialVersionUID = 1L;
    }

    /*
     * Diferència entre record i class?
     * / Un record és com un class especialitzat en guardar dades.
     * / Automàticament defineix:
     * - els camps privats i finals;
     * - el constructor amb tots els components;
     * - els mètodes d’accés;
     * - equals();
     * - hashCode();
     * - toString().
     * - Un record pot implementar interfícies, però no heretar d'altres classes.
     * - Ideal per representar DTO o objectes que venen de formats estructurats
     * (JSON, CSV, BD...)
     */

}
