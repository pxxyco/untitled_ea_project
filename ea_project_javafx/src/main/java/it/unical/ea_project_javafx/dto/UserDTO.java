package it.unical.ea_project_javafx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String password;

    private String profilePhotoUrl;

    // User.Role: ORGANIZER, TRAVELER
    private String role;

    private boolean oauthProvider;

    private String oauthSubject;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;
}
