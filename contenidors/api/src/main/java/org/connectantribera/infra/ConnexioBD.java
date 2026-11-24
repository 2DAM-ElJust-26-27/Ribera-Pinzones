package org.connectantribera.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Configuració immutable. Cada operació obri i tanca la seua connexió JDBC. */
public record ConnexioBD(String url, String usuari, String contrasenya) {
    
    
    public static ConnexioBD desDeEntorn() {
        return new ConnexioBD(requireEnv("AD_DB_URL"), requireEnv("AD_DB_USER"),
                requireEnv("AD_DB_PASSWORD"));
    }

    public Connection obrir() throws SQLException {
        var propietats = new Properties();
        propietats.setProperty("user", usuari);
        propietats.setProperty("password", contrasenya);
        propietats.setProperty("connectTimeout", "2");
        propietats.setProperty("socketTimeout", "2");
        return DriverManager.getConnection(url, propietats);
    }

    private static String requireEnv(String nom) {
        String valor = System.getenv(nom);
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Falta la variable d'entorn " + nom);
        }
        return valor;
    }

    // Evitem que el toString generat pel record incloga la contrasenya.
    @Override public String toString() { return "ConnexioBD[configuració JDBC]"; }
}
