package it.unical.ea_project.repositories;

import it.unical.ea_project.domain.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    //trova tutte le risposte che non sono state eliminate
    List<Response> findByDeletedFalse();

    //trova le risposte associate a una specifica recensione e non eliminate
    List<Response> findByReviewReviewIdAndDeletedFalse(Long reviewId);
}