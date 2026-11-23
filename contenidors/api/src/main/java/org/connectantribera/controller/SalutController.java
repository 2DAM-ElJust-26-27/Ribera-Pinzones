package org.connectantribera.controller;

import io.javalin.http.Context;
import java.sql.SQLException;
import org.connectantribera.service.SalutService;

public final class SalutController {
    private final SalutService servei;

    public SalutController(SalutService servei) { this.servei = servei; }

    public void comprovar(Context ctx) {
        ctx.contentType("application/json; charset=utf-8");
        try {
            if (servei.baseDisponible()) {
                ctx.result("{\"estat\":\"correcte\",\"baseDades\":\"disponible\"}");
            } else {
                noDisponible(ctx);
            }
        } catch (SQLException e) {
            // Registrem el codi d'error sense credencials ni detalls interns al client.
            System.err.println("Comprovació JDBC fallida; SQLState=" + e.getSQLState());
            noDisponible(ctx);
        }
    }

    private static void noDisponible(Context ctx) {
        ctx.status(503).result("{\"estat\":\"no_disponible\",\"baseDades\":\"no_disponible\"}");
    }
}
