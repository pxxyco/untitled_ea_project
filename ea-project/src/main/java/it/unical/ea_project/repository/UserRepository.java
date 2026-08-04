package it.unical.ea_project.repository;

import it.unical.ea_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // cercare un utente per email
    Optional<User> findByEmailIgnoreCase(String email);

    // cercare un utente per username
    Optional<User> findByUsernameIgnoreCase(String username);

    // verificare se un'email o username esistono già
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}