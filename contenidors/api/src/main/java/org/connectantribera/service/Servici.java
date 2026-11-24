package org.connectantribera.service;

import java.sql.SQLException;
import org.connectantribera.infra.ConnexioBD;
import org.connectantribera.repository.Repositori;
import java.sql.Connection;
import java.util.List;

/** Comprovació d'infraestructura; no necessita encara taules del domini. */
public final class Servici {
    private final ConnexioBD configuracio;
    private final Repositori repositori;

    public Servici(ConnexioBD configuracio, Repositori repositori) {
        this.configuracio = configuracio;
        this.repositori = repositori;
    }
}
