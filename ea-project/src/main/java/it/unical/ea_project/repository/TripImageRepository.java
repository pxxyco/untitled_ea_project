package it.unical.ea_project.repository;



import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.domain.TripImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripImageRepository extends JpaRepository<TripImage, Long> {

    // Trova tutte le immagini collegate a un determinato viaggio
    List<TripImage> findByTripOrderByOrderIndexAsc(Trip trip);

    // Rimuove tutte le immagini di un determinato viaggio
    void deleteByTrip(Trip trip);

    List<TripImage> findByTrip_TripIdAndTrip_DeletedAtIsNullOrderByOrderIndexAsc(Long tripId);
}