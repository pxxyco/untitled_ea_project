package it.unical.ea_project.controller;

import it.unical.ea_project.domain.User;
import it.unical.ea_project.dto.UserDTO;
import it.unical.ea_project.service.JwtService;
import it.unical.ea_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setRole(User.Role.valueOf(dto.getRole()));
        user.setOauthProvider(dto.isOauthProvider());
        user.setOauthSubject(dto.getOauthSubject());
        user.setHashedPassword(dto.getPassword());
        User savedUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestParam String email, @RequestParam String password) {
        Optional<User> userOpt = userService.login(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDTO userDto = convertToDto(user);

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("X-Refresh-Token", refreshToken)
                    .body(userDto);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private UserDTO convertToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .oauthProvider(user.isOauthProvider())
                .oauthSubject(user.getOauthSubject())
                .createdAt(user.getCreatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}