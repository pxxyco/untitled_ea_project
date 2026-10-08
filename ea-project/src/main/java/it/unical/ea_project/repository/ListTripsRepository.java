package it.unical.ea_project.repository;

import it.unical.ea_project.domain.ListTrips;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListTripsRepository extends JpaRepository<ListTrips, ListTrips.FavoriteListTripId> {

    //recupera tutti i viaggi associati a una specifica lista
    List<ListTrips> findByFavoriteListId(Long listId);

    //controlla se un viaggio è già presente in una specifica lista
    boolean existsByFavoriteListIdAndTripId(Long listId, Long tripId);
}