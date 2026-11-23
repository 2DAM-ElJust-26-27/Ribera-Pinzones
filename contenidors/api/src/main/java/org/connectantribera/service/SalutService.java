package org.connectantribera.service;

import java.sql.SQLException;
import org.connectantribera.infra.ConnexioBD;

/** Comprovació d'infraestructura; no necessita encara taules del domini. */
public final class SalutService {
    private final ConnexioBD configuracio;

    public SalutService(ConnexioBD configuracio) { this.configuracio = configuracio; }

    public boolean baseDisponible() throws SQLException {
        try (var connexio = configuracio.obrir();
             var consulta = connexio.prepareStatement("SELECT 1");
             var resultat = consulta.executeQuery()) {
            return resultat.next() && resultat.getInt(1) == 1;
        }
    }
}
