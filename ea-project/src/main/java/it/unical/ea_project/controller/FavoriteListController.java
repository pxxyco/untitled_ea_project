package it.unical.ea_project.controller;


import it.unical.ea_project.domain.FavoriteList;
import it.unical.ea_project.service.FavoriteListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-lists")
@RequiredArgsConstructor
public class FavoriteListController {

    private final FavoriteListService favoriteListService;

    public record FavoriteListRequest(Long userId, FavoriteList.Visibility visibility) {}

    @PostMapping
    public ResponseEntity<FavoriteList> createList(@RequestBody FavoriteListRequest request) {
        FavoriteList newList = favoriteListService.createFavoriteList(
                request.userId(),
                request.visibility()
        );
        return new ResponseEntity<>(newList, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteList>> getListsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteListService.getActiveListsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FavoriteList> getListById(@PathVariable Long id) {
        return ResponseEntity.ok(favoriteListService.getListById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        favoriteListService.softDeleteList(id);
        return ResponseEntity.noContent().build();
    }
}