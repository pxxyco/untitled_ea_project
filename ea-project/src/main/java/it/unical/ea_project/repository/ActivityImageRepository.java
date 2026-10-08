package it.unical.ea_project.repository;


import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.domain.ActivityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityImageRepository extends JpaRepository<ActivityImage, Long> {

    // trova tutte le immagini associate a una specifica attività, ordinate per orderIndex
    List<ActivityImage> findByActivityOrderByOrderIndexAsc(Activity activity);

    // Recupera le immagini di PIÙ attività con una sola query
    @Query("""
        SELECT ai
        FROM ActivityImage ai
        WHERE ai.activity.activityId IN :activityIds
        ORDER BY
            ai.activity.activityId ASC,
            ai.orderIndex ASC
        """)
    List<ActivityImage> findByActivityIds(
            @Param("activityIds")
            List<Long> activityIds
    );

    // rimuove tutte le immagini legate a un'attività
    void deleteByActivity(Activity activity);
}