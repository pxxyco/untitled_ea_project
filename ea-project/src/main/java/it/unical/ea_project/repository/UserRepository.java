package it.unical.ea_project.repository;

import it.unical.ea_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}

//AAAA Repository di prova se crea conflitti accetta il tuo