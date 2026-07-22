package it.unical.ea_project.repository;


import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.domain.ActivityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityImageRepository extends JpaRepository<ActivityImage, Long> {

    // trova tutte le immagini associate a una specifica attività, ordinate per orderIndex
    List<ActivityImage> findByActivityOrderByOrderIndexAsc(Activity activity);

    // rimuove tutte le immagini legate a un'attività
    void deleteByActivity(Activity activity);
}