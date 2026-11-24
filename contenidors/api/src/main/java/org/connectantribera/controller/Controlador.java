package org.connectantribera.controller;

import io.javalin.http.Context;
import java.sql.SQLException;
import org.connectantribera.service.SalutService;

public final class Controlador {
    private final Service servei;
    private final JsonView vista;

    public Controlador(Service servei, JsonView vista) { this.servei = servei; this.vista = vista; }

    public void get(Context ctx) throws SQLException {
        ctx.contentType("application/json; charset=utf-8");
        if (servei.baseDisponible()) {
            vista.ok(ctx);
        } else {
            noDisponible(ctx);
        }
    }

    private void noDisponible(Context ctx) {
        vista.error(ctx, 503, "El servei no està disponible");
    }
    
}
