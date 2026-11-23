package org.connectantribera;

import io.javalin.Javalin;
import org.connectantribera.controller.SalutController;
import org.connectantribera.infra.ConnexioBD;
import org.connectantribera.service.SalutService;

/** Punt de composició inicial. Les rutes del directori s'afegiran posteriorment. */
public final class Main {
    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("API_PORT", "8080"));
        var configuracio = ConnexioBD.desDeEntorn();
        var controlador = new SalutController(new SalutService(configuracio));
        var app = Javalin.create(config -> {
            config.routes.get("/api/estat", ctx -> ctx.contentType("application/json; charset=utf-8")
                    .result("{\"estat\":\"en_marxa\"}"));
            config.routes.get("/api/salut", controlador::comprovar);
        });
        app.start("0.0.0.0", port);
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }
}
