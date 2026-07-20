package it.unical.ea_project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unical.ea_project.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

}//AAAA Repository di prova se crea conflitti accetta il tuo