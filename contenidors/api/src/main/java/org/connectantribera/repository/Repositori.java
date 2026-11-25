package org.connectantribera.repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.connectantribera.model.Tipus;

public final class Repositori {
    public List<Tipus> llistarTipus(Connection connexio) throws SQLException {
        String sql = "SELECT id, nom FROM ribera.categoria_servei ORDER BY nom";
        List<Tipus> llista = new ArrayList<>();

        try (PreparedStatement sentencia = connexio.prepareStatement(sql);
            ResultSet resultat = sentencia.executeQuery()) {
            while (resultat.next()) {
                llista.add(maparTipus(resultat));
            }
        }

        return llista;
    }

    private Tipus maparTipus(ResultSet resultat) throws SQLException {
        return new Tipus(resultat.getLong("id"), resultat.getString("nom"));
    }
}
