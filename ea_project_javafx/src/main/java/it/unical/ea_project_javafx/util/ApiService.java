package it.unical.ea_project_javafx.util;

import it.unical.ea_project_javafx.model.UserSession;
import javafx.concurrent.Task;
import lombok.Setter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ApiService {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static final String BASE_URL = "http://localhost:8080";
    @Setter
    private static Runnable onNetworkFailureGlobal;

    public static void call(String url, String body, String method,
                            Consumer<HttpResponse<String>> onSuccess,
                            Runnable onFailure,
                            Consumer<Boolean> loadingSetter) {

        if (loadingSetter != null) loadingSetter.accept(true);

        AtomicBoolean backendReachable = new AtomicBoolean(true);

        Task<HttpResponse<String>> task = new Task<>() {
            @Override
            protected HttpResponse<String> call() throws Exception {
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(30));

                String token = UserSession.getInstance().getAccessToken();
                if (token != null && !token.isBlank()) {
                    builder.header("Authorization", "Bearer " + token);
                }

                HttpRequest.BodyPublisher bodyPublisher = (body == null || body.isEmpty())
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body);

                switch (method.toUpperCase()) {
                    case "POST" -> {
                        builder.header("Content-Type", "application/json");
                        builder.POST(bodyPublisher);
                    }
                    case "PUT" -> {
                        builder.header("Content-Type", "application/json");
                        builder.PUT(bodyPublisher);
                    }
                    case "PATCH" -> {
                        builder.header("Content-Type", "application/json");
                        builder.method("PATCH", bodyPublisher);
                    }
                    case "DELETE" -> builder.DELETE();
                    default -> builder.GET();
                }

                try {
                    return CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                } catch (Exception ex) {
                    // still on the background thread here: safe to block briefly
                    backendReachable.set(isBackendReachable());
                    throw ex;
                }
            }
        };

        task.setOnFailed(e -> {
            if (loadingSetter != null) loadingSetter.accept(false);
            if (!backendReachable.get() && onNetworkFailureGlobal != null) {
                onNetworkFailureGlobal.run();
                return;
            }
            if (onFailure != null) onFailure.run();
        });

        task.setOnSucceeded(e -> {
            if (loadingSetter != null) loadingSetter.accept(false);
            onSuccess.accept(task.getValue());
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void get(String url,
                           Consumer<HttpResponse<String>> onSuccess,
                           Runnable onFailure,
                           Consumer<Boolean> loadingSetter) {
        call(url, null, "GET", onSuccess, onFailure, loadingSetter);
    }

    public static void post(String url, String body,
                            Consumer<HttpResponse<String>> onSuccess,
                            Runnable onFailure,
                            Consumer<Boolean> loadingSetter) {
        call(url, body, "POST", onSuccess, onFailure, loadingSetter);
    }

    public static void put(String url, String body,
                           Consumer<HttpResponse<String>> onSuccess,
                           Runnable onFailure,
                           Consumer<Boolean> loadingSetter) {
        call(url, body, "PUT", onSuccess, onFailure, loadingSetter);
    }

    public static void delete(String url,
                              Consumer<HttpResponse<String>> onSuccess,
                              Runnable onFailure,
                              Consumer<Boolean> loadingSetter) {
        call(url, null, "DELETE", onSuccess, onFailure, loadingSetter);
    }

    public static void patch(String url, String body,
                             Consumer<HttpResponse<String>> onSuccess,
                             Runnable onFailure,
                             Consumer<Boolean> loadingSetter) {
        call(url, body, "PATCH", onSuccess, onFailure, loadingSetter);
    }

    public static boolean isBackendReachable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/health"))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();
            HttpResponse<Void> response = CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}