package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.User;
import it.unical.ea_project.repository.UserRepository;
import it.unical.ea_project.service.LoginAttemptService;
import it.unical.ea_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    @Override
    public Optional<User> login(String identifier, String password) {
        String normalizedIdentifier = identifier == null ? "" : identifier.trim();
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(normalizedIdentifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsernameIgnoreCase(normalizedIdentifier);
        }

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getHashedPassword())) {
            loginAttemptService.loginSucceeded(normalizedIdentifier);
            return userOpt;
        } else {
            loginAttemptService.loginFailed(normalizedIdentifier);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username già in uso.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già registrata.");
        }
        user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
        return userRepository.save(user);
    }
}