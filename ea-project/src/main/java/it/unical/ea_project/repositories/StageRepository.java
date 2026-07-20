package it.unical.ea_project.repositories;

import it.unical.ea_project.domain.Stage;
import it.unical.ea_project.domain.Stage.StageCategory;
import it.unical.ea_project.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    // Recupera tutte le tappe di un determinato viaggio, ordinate per giorno e per ordine all'interno del giorno
    // Mostra l'itinerario corretto all'utente
    List<Stage> findByTripOrderByDayAscOrderInDayAsc(Trip trip);

    // Trova tutte le tappe di un viaggio filtrate per una specifica categoria (es. tutte le escursioni o tutti gli hotel)
    List<Stage> findByTripAndCategory(Trip trip, StageCategory category);

    // Trova le tappe di un viaggio pianificate per un giorno specifico (es. giorno 1, giorno 2...)
    List<Stage> findByTripAndDayOrderByOrderInDayAsc(Trip trip, Integer day);

    // Cerca tappe in una determinata città (utile se si vuole fare una ricerca geografica globale tra le tappe)
    List<Stage> findByCityIgnoreCase(String city);
}
