
package org.pinzones;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class FitxersXML {

    public static void leer(Path archivo) throws IOException {

        try {
            // Crear el analizador XML
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();

            // Leer el archivo XML
            Document documento = builder.parse(archivo.toFile());

            // Obtener la raíz del documento
            Element raiz = documento.getDocumentElement();

            // Obtener todas las entidades
            NodeList entidades = raiz.getElementsByTagName("entitat");

            System.out.println("\nArchivo: " + archivo.getFileName());
            System.out.println("Entidades encontradas: " + entidades.getLength());

            // Recorrer las entidades
            for (int i = 0; i < entidades.getLength(); i++) {

                Element entidad = (Element) entidades.item(i);

                // Leer el atributo codi
                String codigo = entidad.getAttribute("codi");

                System.out.println("\n----------------------");
                System.out.println("Código: " + codigo);

                // Recorrer los elementos de cada entidad
                NodeList campos = entidad.getChildNodes();

                for (int j = 0; j < campos.getLength(); j++) {

                    Node campo = campos.item(j);

                    // Comprobar que sea un elemento XML
                    if (campo.getNodeType() == Node.ELEMENT_NODE) {

                        String nombre = campo.getNodeName();
                        String valor = campo.getTextContent().trim();

                        System.out.println(nombre + ": " + valor);
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Error leyendo el XML: " + e.getMessage(), e);
        }
    }
}

