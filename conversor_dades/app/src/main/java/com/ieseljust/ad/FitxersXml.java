package com.ieseljust.ad;

import java.io.IOException;
import java.nio.file.Path;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** Exemples de l'apartat 4: lectura i escriptura d'XML. */
public final class FitxersXml {

    private static final Path DADES = Path.of("dades");

    /** DOM carrega l'XML complet i permet navegar per l'arbre. */
    public static void llegirAmbDom() throws Exception {
        Document document = nouBuilderSegur().parse(
                DADES.resolve("biblioteca.xml").toFile());

        NodeList llibres = document.getElementsByTagName("llibre");
        for (int i = 0; i < llibres.getLength(); i++) {
            Element llibre = (Element) llibres.item(i);
            String id = llibre.getAttribute("id");
            String titol = textObligatori(llibre, "titol");
            String autor = textObligatori(llibre, "autor");
            System.out.printf("%s: %s — %s%n", id, titol, autor);
        }
    }

    /** SAX produeix esdeveniments i no conserva tot el document en memòria. */
    public static void llegirAmbSax() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        SAXParser parser = factory.newSAXParser();
        parser.parse(DADES.resolve("biblioteca.xml").toFile(), new GestorTitols());
    }

    /** Construeix un arbre DOM nou i el transforma en un fitxer XML. */
    public static void escriureAmbDom() throws Exception {
        Document document = nouBuilderSegur().newDocument();
        Element biblioteca = document.createElement("biblioteca");
        document.appendChild(biblioteca);

        afegirLlibre(document, biblioteca, "1",
                "Tirant lo Blanc", "Joanot Martorell");
        afegirLlibre(document, biblioteca, "2",
                "El llibre de les bèsties", "Ramon Llull");

        escriureDocument(document, DADES.resolve("biblioteca_generada.xml"));
        System.out.println("XML generat correctament.");
    }

    /**
     * Activitat: valida els camps de cada producte i genera un XML
     * amb els que superen el preu mínim.
     */
    public static void activitatCataleg() throws Exception {
        double preuMinim = 20.0;
        Document entrada = nouBuilderSegur().parse(
                DADES.resolve("productes.xml").toFile());
        Document eixida = nouBuilderSegur().newDocument();
        Element catalegEixida = eixida.createElement("productes");
        eixida.appendChild(catalegEixida);

        NodeList productes = entrada.getElementsByTagName("producte");
        for (int i = 0; i < productes.getLength(); i++) {
            Element producte = (Element) productes.item(i);
            try {
                String id = producte.getAttribute("id");
                String nom = textObligatori(producte, "nom");
                double preu = Double.parseDouble(textObligatori(producte, "preu"));

                if (id.isBlank() || preu < 0) {
                    throw new IllegalArgumentException("identificador o preu no vàlid");
                }
                if (preu > preuMinim) {
                    Element copia = (Element) eixida.importNode(producte, true);
                    catalegEixida.appendChild(copia);
                }
            } catch (RuntimeException e) {
                System.err.printf("Producte %d descartat: %s%n", i + 1, e.getMessage());
            }
        }

        Path destinacio = DADES.resolve("productes_mes_20.xml");
        escriureDocument(eixida, destinacio);
        System.out.println("Catàleg filtrat: " + destinacio);
    }

    private static DocumentBuilder nouBuilderSegur()
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder();
    }

    private static String textObligatori(Element pare, String etiqueta) {
        NodeList nodes = pare.getElementsByTagName(etiqueta);
        if (nodes.getLength() == 0 || nodes.item(0).getTextContent().isBlank()) {
            throw new IllegalArgumentException("falta " + etiqueta);
        }
        return nodes.item(0).getTextContent().trim();
    }

    private static void afegirLlibre(
            Document document, Element biblioteca, String id,
            String titol, String autor) {
        Element llibre = document.createElement("llibre");
        llibre.setAttribute("id", id);
        biblioteca.appendChild(llibre);

        Element elementTitol = document.createElement("titol");
        elementTitol.setTextContent(titol);
        llibre.appendChild(elementTitol);

        Element elementAutor = document.createElement("autor");
        elementAutor.setTextContent(autor);
        llibre.appendChild(elementAutor);
    }

    private static void escriureDocument(Document document, Path ruta)
            throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(ruta.toFile()));
    }

    private static final class GestorTitols extends DefaultHandler {
        private final StringBuilder text = new StringBuilder();
        private boolean dinsTitol;

        @Override
        public void startElement(
                String uri, String localName, String qName, Attributes attributes) {
            if ("titol".equals(qName)) {
                dinsTitol = true;
                text.setLength(0);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (dinsTitol) {
                text.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if ("titol".equals(qName)) {
                System.out.println(text.toString().trim());
                dinsTitol = false;
            }
        }
    }
}
