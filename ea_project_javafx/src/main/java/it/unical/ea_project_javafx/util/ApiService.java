package it.unical.ea_project_javafx.util;

import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

public class ApiService {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static final String BASE_URL = "http://localhost:8080";
    private static Runnable onNetworkFailureGlobal;

    public static void setOnNetworkFailureGlobal(Runnable action) {
        onNetworkFailureGlobal = action;
    }

    public static void call(String url, String body, String method,
                            Consumer<HttpResponse<String>> onSuccess,
                            Runnable onFailure,
                            Consumer<Boolean> loadingSetter) {

        if (loadingSetter != null) loadingSetter.accept(true);

        Task<HttpResponse<String>> task = new Task<>() {
            @Override
            protected HttpResponse<String> call() throws Exception {
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(30));

                if ("POST".equalsIgnoreCase(method)) {
                    builder.header("Content-Type", "application/json");
                    builder.POST(body == null || body.isEmpty()
                            ? HttpRequest.BodyPublishers.noBody()
                            : HttpRequest.BodyPublishers.ofString(body));
                }
                return CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            }
        };

        task.setOnFailed(e -> {
            if (loadingSetter != null) loadingSetter.accept(false);
            if (!isBackendReachable()) {
                if (onNetworkFailureGlobal != null) {
                    onNetworkFailureGlobal.run();
                    return;
                }
            }
            if (onFailure != null) onFailure.run();
        });

        task.setOnSucceeded(e -> {
            if (loadingSetter != null) loadingSetter.accept(false);
            onSuccess.accept(task.getValue());
        });

        new Thread(task).start();
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