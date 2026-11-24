package org.connectantribera.controller;

import io.javalin.http.Context;
import java.sql.SQLException;
import org.connectantribera.service.Servici;
import java.util.List;
import org.connectantribera.view.JsonView;
import org.connectantribera.infra.ConnexioBD;
import org.connectantribera.repository.Repositori;

public final class Controlador {
    private final Servici servei;
    private final JsonView vista;

    public Controlador(Servici servei, JsonView vista) { this.servei = servei; this.vista = vista; }

    public void get(Context ctx) throws SQLException {
        String tipus = ctx.pathParam("tipus");
        ctx.json(servei.llistarTipus(tipus));

        if (tipus == null || tipus.trim().isEmpty()) {
            throw new IllegalArgumentException("Falta el paràmetre 'tipus'");
        }
    }
}
