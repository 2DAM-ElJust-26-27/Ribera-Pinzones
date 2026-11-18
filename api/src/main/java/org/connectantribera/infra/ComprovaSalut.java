package org.connectantribera.infra;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Client HTTP del healthcheck: no instal·lem curl dins de la imatge Java. */
public final class ComprovaSalut {
    public static void main(String[] args) {
        String port = System.getenv().getOrDefault("API_PORT", "8080");
        try {
            var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
            var peticio = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/salut"))
                    .timeout(Duration.ofSeconds(4)).GET().build();
            var resposta = client.send(peticio, HttpResponse.BodyHandlers.discarding());
            if (resposta.statusCode() != 200) System.exit(1);
        } catch (Exception e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            System.exit(1);
        }
    }
}
