package it.unical.ea_project.service;


import it.unical.ea_project.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);

    Optional<User> login(String identifier, String password);
    User registerUser(User user);
}