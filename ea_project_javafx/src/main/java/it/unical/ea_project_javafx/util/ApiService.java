package it.unical.ea_project_javafx.util;

import it.unical.ea_project_javafx.model.UserSession;
import javafx.concurrent.Task;
import lombok.Setter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

public class ApiService {

    public enum Method { GET, POST, PUT, DELETE }

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static final String BASE_URL = "http://localhost:8080";

    @Setter
    private static Runnable onNetworkFailureGlobal;

    public static void get(String url, Consumer<HttpResponse<String>> onSuccess,
                           Runnable onFailure, Consumer<Boolean> loadingSetter) {
        call(url, null, Method.GET, onSuccess, onFailure, loadingSetter);
    }

    public static void post(String url, String body, Consumer<HttpResponse<String>> onSuccess,
                            Runnable onFailure, Consumer<Boolean> loadingSetter) {
        call(url, body, Method.POST, onSuccess, onFailure, loadingSetter);
    }

    public static void put(String url, String body, Consumer<HttpResponse<String>> onSuccess,
                           Runnable onFailure, Consumer<Boolean> loadingSetter) {
        call(url, body, Method.PUT, onSuccess, onFailure, loadingSetter);
    }

    public static void delete(String url, Consumer<HttpResponse<String>> onSuccess,
                              Runnable onFailure, Consumer<Boolean> loadingSetter) {
        call(url, null, Method.DELETE, onSuccess, onFailure, loadingSetter);
    }


    public static void call(String url, String body, Method method,
                            Consumer<HttpResponse<String>> onSuccess,
                            Runnable onFailure,
                            Consumer<Boolean> loadingSetter) {

        setLoading(loadingSetter, true);

        Task<HttpResponse<String>> task = new Task<>() {
            @Override
            protected HttpResponse<String> call() throws Exception {
                return CLIENT.send(buildRequest(url, body, method), HttpResponse.BodyHandlers.ofString());
            }
        };

        task.setOnSucceeded(e -> {
            setLoading(loadingSetter, false);
            onSuccess.accept(task.getValue());
        });

        task.setOnFailed(e -> {
            setLoading(loadingSetter, false);
            handleFailure(onFailure);
        });

        new Thread(task).start();
    }

    private static HttpRequest buildRequest(String url, String body, Method method) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30));

        String token = UserSession.getInstance().getAccessToken();
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpRequest.BodyPublisher publisher = (body == null || body.isEmpty())
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body);

        switch (method) {
            case POST -> builder.header("Content-Type", "application/json").POST(publisher);
            case PUT -> builder.header("Content-Type", "application/json").PUT(publisher);
            case DELETE -> {
                if (body != null && !body.isEmpty()) {
                    builder.header("Content-Type", "application/json");
                    builder.method("DELETE", publisher);
                } else {
                    builder.DELETE();
                }
            }
            case GET -> builder.GET();
        }

        return builder.build();
    }

    private static void setLoading(Consumer<Boolean> loadingSetter, boolean value) {
        if (loadingSetter != null) loadingSetter.accept(value);
    }

    private static void handleFailure(Runnable onFailure) {
        if (!isBackendReachable() && onNetworkFailureGlobal != null) {
            onNetworkFailureGlobal.run();
            return;
        }
        if (onFailure != null) onFailure.run();
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