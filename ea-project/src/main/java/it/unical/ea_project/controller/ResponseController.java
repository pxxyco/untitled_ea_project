package it.unical.ea_project.controller;


import it.unical.ea_project.domain.Response;
import it.unical.ea_project.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    public record ResponseRequest(Long organizerId, Long reviewId, String comment) {}

    @PostMapping
    public ResponseEntity<Response> createResponse(@RequestBody ResponseRequest request) {
        Response newResponse = responseService.createResponse(
                request.organizerId(),
                request.reviewId(),
                request.comment()
        );
        return new ResponseEntity<>(newResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Response>> getAllResponses() {
        return ResponseEntity.ok(responseService.getAllActiveResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getResponseById(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getResponseById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponse(@PathVariable Long id) {
        responseService.softDeleteResponse(id);
        return ResponseEntity.noContent().build();
    }
}