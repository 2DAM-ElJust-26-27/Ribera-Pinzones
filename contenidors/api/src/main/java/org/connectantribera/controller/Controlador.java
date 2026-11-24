package org.connectantribera.controller;

import io.javalin.http.Context;
import java.sql.SQLException;
import org.connectantribera.service.SalutService;

public final class Controlador {
    private final Service servei;
    private final JsonView vista;

    public Controlador(Service servei, JsonView vista) { this.servei = servei; this.vista = vista; }

    public void get(Context ctx) throws SQLException {
        String tipus = ctx.pathParam("tipus");

        if (tipus.equals("salut")) {
            if (servei.baseDisponible()) {
                ctx.status(200).json("{\"estat\":\"correcte\"}");
            } else {
                ctx.status(503).json("{\"estat\":\"no_disponible\"}");
            }
            return;
        }

        List<?> resultat = switch (tipus) {
            case "cataleg" -> servei.llistarCataleg();
            case "centres" -> servei.llistarCentres();
            case "contactes" -> servei.llistarContactes();
            case "entitats" -> servei.llistarEntitats();
            case "horaris" -> servei.llistarHoraris();
            case "serveis" -> servei.llistarServeis();
            case "territori" -> servei.llistarTerritori();
            default -> throw new IllegalArgumentException(
                "Tipus desconegut: " + tipus
            );
        };

        ctx.json(resultat);
    }

}
