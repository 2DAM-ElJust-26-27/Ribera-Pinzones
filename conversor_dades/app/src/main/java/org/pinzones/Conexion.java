package org.pinzones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexion {

    private static final String URL =
            "jdbc:postgresql://localhost:5435/ribera"; // Puerto y nombre DB

    private static final String USUARIO = "ribera";
    private static final String PASSWORD = "2dam";

    public static Connection conectar() throws SQLException {

        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
