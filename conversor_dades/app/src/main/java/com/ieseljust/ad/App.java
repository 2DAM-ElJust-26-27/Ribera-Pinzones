package com.ieseljust.ad;

import java.nio.file.Path;

/** Llançador senzill dels exemples de la unitat. */
public class App {

    public static void main(String[] args) {
        if (args.length == 0) {
            mostraAjuda();
            return;
        }

        try {
            switch (args[0]) {
                // Apartats 1 i 2
                case "fitxers-info" ->
                    FitxersText.mostraInfoFitxer("dades/entrants.json");
                case "fitxers-operacions" -> FitxersText.operacions();
                case "fitxers-recorregut" -> FitxersText.recorreCarpeta();
                case "fitxers-activitat" -> FitxersText.exercici_1("dades");
                case "text-bytes" -> FitxersText.exemple4();
                case "text-proces-bytes" -> FitxersText.exemple5();
                case "text-caracters" -> FitxersText.exemple6();
                case "text-decoradors" -> FitxersText.exemple7();
                case "text-conveniencia" -> FitxersText.exemple8();
                case "text-activitat" -> FitxersText.copiaLiniesNoBuides(
                        Path.of("dades", "text_amb_buits.txt"),
                        Path.of("dades", "linies_numerades.txt"));

                // Apartat 3
                case "binaris-primitius" -> DadesBinaries.tipusPrimitius();
                case "binaris-aleatori" -> DadesBinaries.accesAleatori();
                case "binaris-objectes" -> DadesBinaries.serialitzacioObjectes();
                case "binaris-activitat" -> DadesBinaries.activitatTemperatures();

                // Apartat 4
                case "xml-dom" -> FitxersXml.llegirAmbDom();
                case "xml-sax" -> FitxersXml.llegirAmbSax();
                case "xml-escriure" -> FitxersXml.escriureAmbDom();
                case "xml-activitat" -> FitxersXml.activitatCataleg();

                // Apartat 5
                case "json-objecte" -> FitxersJson.llegirObjecte();
                case "json-llista" -> FitxersJson.llegirLlista();
                case "json-escriure" -> FitxersJson.escriureObjectes();
                case "json-arbre" -> FitxersJson.llegirArbre();
                case "json-activitat" -> FitxersJson.activitatLlibres();

                // Apartat 6
                case "csv-llegir" -> CsvIProperties.llegirCsv();
                case "csv-escriure" -> CsvIProperties.escriureCsv();
                case "properties-llegir" -> CsvIProperties.llegirProperties();
                case "properties-escriure" -> CsvIProperties.escriureProperties();
                case "csv-activitat" -> CsvIProperties.activitatCsvAJson();

                default -> {
                    System.err.println("Exemple desconegut: " + args[0]);
                    mostraAjuda();
                }
            }
        } catch (Exception e) {
            System.err.println("No s'ha pogut executar l'exemple: " + e.getMessage());
        }
    }

    private static void mostraAjuda() {
        System.out.println("Ús: ./gradlew run --args=\"nom-exemple\"");
        System.out.println("Exemples: fitxers-*, text-*, binaris-*, xml-*, json-*,");
        System.out.println("          csv-* i properties-*");
    }
}
