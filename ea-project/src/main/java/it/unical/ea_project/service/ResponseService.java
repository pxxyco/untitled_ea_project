package it.unical.ea_project.service;

import it.unical.ea_project.domain.Response;
import java.util.List;

public interface ResponseService {
    Response createResponse(Long organizerId, Long reviewId, String comment);
    List<Response> getAllActiveResponses();
    Response getResponseById(Long id);
    void softDeleteResponse(Long id);
}