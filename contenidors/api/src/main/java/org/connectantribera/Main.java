package org.connectantribera;

import io.javalin.Javalin;
import org.connectantribera.controller.SalutController;
import org.connectantribera.infra.ConnexioBD;
import org.connectantribera.service.SalutService;

/** Punt de composició inicial. Les rutes del directori s'afegiran posteriorment. */
public final class Main {
    public static void main(String[] args) {
        ConnexioBD configuracio = new ConnexioBD.desDeEntorn();
        Repositori repositori = new Repositori();
        Servici servei = new SalutService(configuracio, repositori);
        JsonView vista = new JsonView(configuracio);
        Controlador controlador = new Controlador(servei, vista);
        int port = Integer.parseInt(System.getenv().getOrDefault("API_PORT", "8080"));

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
            config.bundledPlugins.enableCors(
                cors -> cors.addRule(rule -> rule.anyHost())
            );
            config.routes.get("/api/{tipus}", ctx -> controlador.get(ctx));
            config.routes.exception(IllegalArgumentException.class, 
                (error, ctx) -> vista.error(ctx, 400, error.getMessage())
            );
            config.routes.exception(SQLException.class, 
                (error, ctx) -> vista.error(ctx, 500, "No s'ha pogut consultar el catàleg")
            );
        });
        app.start("0.0.0.0", port);
    }
}
