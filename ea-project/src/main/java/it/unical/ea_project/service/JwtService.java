package it.unical.ea_project.service;

import it.unical.ea_project.domain.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractUsername(String token);
    boolean isTokenValid(String token, User user);
}