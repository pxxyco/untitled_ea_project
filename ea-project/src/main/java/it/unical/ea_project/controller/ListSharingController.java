package it.unical.ea_project.controller;

import it.unical.ea_project.domain.ListSharing;
import it.unical.ea_project.service.ListSharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/list-sharings")
@RequiredArgsConstructor
public class ListSharingController {

    private final ListSharingService listSharingService;

    public record ShareListRequest(Long listId, Long userId, ListSharing.SharingPermission permission) {}

    @PostMapping
    public ResponseEntity<ListSharing> shareList(@RequestBody ShareListRequest request) {
        ListSharing shared = listSharingService.shareList(
                request.listId(),
                request.userId(),
                request.permission()
        );
        return new ResponseEntity<>(shared, HttpStatus.CREATED);
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<List<ListSharing>> getSharingsByList(@PathVariable Long listId) {
        return ResponseEntity.ok(listSharingService.getSharingsByListId(listId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ListSharing>> getSharedListsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(listSharingService.getSharedListsForUser(userId));
    }

    @PutMapping("/permission")
    public ResponseEntity<Void> updatePermission(@RequestBody ShareListRequest request) {
        listSharingService.updatePermission(request.listId(), request.userId(), request.permission());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/list/{listId}/user/{userId}")
    public ResponseEntity<Void> revokeSharing(@PathVariable Long listId, @PathVariable Long userId) {
        listSharingService.revokeSharing(listId, userId);
        return ResponseEntity.noContent().build();
    }
}