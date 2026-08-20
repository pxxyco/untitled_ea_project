package it.unical.ea_project.service;

public interface LoginAttemptService {
    void loginFailed(String identifier);
    void loginSucceeded(String identifier);
}