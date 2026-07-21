package it.unical.ea_project.repositories;


import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // trova tutte le attività create da uno specifico utente
    List<Activity> findByCreatedBy(User createdBy);

    // trova le attività in base allo stato
    List<Activity> findByStatus(Activity.Status status);

    // trova le attività filtrate per città e categoria
    List<Activity> findByCityAndCategory(String city, Activity.Category category);
}