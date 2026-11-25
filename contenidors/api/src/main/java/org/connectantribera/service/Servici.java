package org.connectantribera.service;

import java.sql.SQLException;
import org.connectantribera.infra.ConnexioBD;
import java.sql.Connection;
import java.util.List;
import org.connectantribera.repository.Repositori;

/** Comprovació d'infraestructura; no necessita encara taules del domini. */
public final class Servici {
    private final ConnexioBD configuracio;
    private final Repositori repositori;

    public Servici(ConnexioBD configuracio, Repositori repositori) {
        this.configuracio = configuracio;
        this.repositori = repositori;
    }

    public List<?> llistarTipus(String tipus) throws SQLException {
        try (Connection connexio = configuracio.obrir()) {
            return repositori.llistarTipus(connexio);
        }
    }

    public List<?> llistarCategories(String tipus) throws SQLException {
       try (Connection connexio = configuracio.obrir()) {
            return switch (tipus) {
                case "cataleg" -> repositori.llistarTipus(connexio);
                case "centres" -> repositori.llistarTipus(connexio);
                case "contactes" -> repositori.llistarTipus(connexio);
                case "entitats" -> repositori.llistarTipus(connexio);
                case "horaris" -> repositori.llistarTipus(connexio);
                case "serveis" -> repositori.llistarTipus(connexio);
                case "territori" -> repositori.llistarTipus(connexio);
                default -> throw new IllegalArgumentException(
                    "Tipus desconegut: " + tipus
                );
            };
        }
    }
}
