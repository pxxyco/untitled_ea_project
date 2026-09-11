package it.unical.ea_project_javafx.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.dto.ReviewDTO;
import it.unical.ea_project_javafx.util.ApiService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

// TODO: replace generic status code range check (>=200 && <300) with specific status codes per case

public class ReviewService {

    private static final String BASE_URL = ApiService.BASE_URL + "/api/reviews" ;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                    src == null ? null : new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                    LocalDateTime.parse(json.getAsString()))
            .create();

    public static void create(ReviewDTO review, Runnable onSuccess, Runnable onFailure, Consumer<Boolean> loading) {
        ApiService.post(
                BASE_URL,
                GSON.toJson(review),
                response -> {
                    if(response.statusCode() >= 200 && response.statusCode() <= 300) {
                        if(onSuccess != null ) onSuccess.run();
                    }
                    else  if(onFailure != null) onFailure.run();
                },
                onFailure,
                loading
        );
    }

    public static void getActivityReview(Long id, Consumer<List<ReviewDTO>> onSuccess, Runnable onFailure, Consumer<Boolean> loading) {
        ApiService.get(
                BASE_URL + "/activity/" + id,
                response -> {
                    if(response.statusCode() >= 200 && response.statusCode() <= 300) {
                        List<ReviewDTO> reviews = GSON.fromJson(response.body(), new TypeToken<List<ReviewDTO>>() {}.getType());
                        if (onSuccess != null) onSuccess.accept(reviews);
                    }
                    else  if(onFailure != null) onFailure.run();
                },
                onFailure,
                loading
        );
    }

    public static void getTripReview(Long id, Consumer<List<ReviewDTO>> onSuccess, Runnable onFailure, Consumer<Boolean> loading) {
        ApiService.get(
                BASE_URL + "/trip/" + id,
                response -> {
                    if(response.statusCode() >= 200 && response.statusCode() <= 300) {
                        List<ReviewDTO> reviews = GSON.fromJson(response.body(), new TypeToken<List<ReviewDTO>>() {}.getType());
                        if (onSuccess != null) onSuccess.accept(reviews);
                    }
                    else  if(onFailure != null) onFailure.run();
                },
                onFailure,
                loading
        );
    }
}
