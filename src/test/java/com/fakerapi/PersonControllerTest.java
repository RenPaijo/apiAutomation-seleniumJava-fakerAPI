package com.fakerapi;

import com.fakerapi.controller.PersonController;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonControllerTest {

    @Test
    void retriesBadGatewayThenReturnsPersons() throws IOException {
        AtomicInteger requests = new AtomicInteger();
        HttpServer server = startServer(requests, 502, 200);
        try {
            PersonController controller = new PersonController(baseUri(server));

            assertEquals(1, controller.getPersons(1, "male", "1990-01-01", "2000-12-31").size());
            assertEquals(2, requests.get());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void persistentBadGatewayIsReportedAfterThreeAttempts() throws IOException {
        AtomicInteger requests = new AtomicInteger();
        HttpServer server = startServer(requests, 502, 502, 502);
        try {
            PersonController controller = new PersonController(baseUri(server));

            PersonController.BadGatewayException error = assertThrows(PersonController.BadGatewayException.class,
                    () -> controller.getPersons(1, "male", "1990-01-01", "2000-12-31"));
            assertTrue(error.getMessage().contains("HTTP 502"));
            assertEquals(3, requests.get());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void otherErrorsAreNotRetried() throws IOException {
        AtomicInteger requests = new AtomicInteger();
        HttpServer server = startServer(requests, 500);
        try {
            PersonController controller = new PersonController(baseUri(server));

            IllegalStateException error = assertThrows(IllegalStateException.class,
                    () -> controller.getPersons(1, "male", "1990-01-01", "2000-12-31"));
            assertTrue(error.getMessage().contains("HTTP 500"));
            assertEquals(1, requests.get());
        } finally {
            server.stop(0);
        }
    }

    private static HttpServer startServer(AtomicInteger requests, int... statuses) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/v2/persons", exchange -> {
            int request = requests.getAndIncrement();
            int status = statuses[Math.min(request, statuses.length - 1)];
            byte[] body = (status == 200
                    ? "{\"data\":[{\"firstname\":\"Test\",\"lastname\":\"User\",\"gender\":\"male\",\"birthday\":\"1995-01-01\"}]}"
                    : "Bad Gateway").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", status == 200 ? "application/json" : "text/plain");
            exchange.sendResponseHeaders(status, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        return server;
    }

    private static String baseUri(HttpServer server) {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }
}
