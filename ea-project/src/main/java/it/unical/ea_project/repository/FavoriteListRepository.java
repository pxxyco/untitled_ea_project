package it.unical.ea_project.repository;

import it.unical.ea_project.domain.FavoriteList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteListRepository extends JpaRepository<FavoriteList, Long> {

    //recupera le liste attive di un utente
    List<FavoriteList> findByUser_IdAndDeletedFalse(Long userId);

    //recupera tutte le liste pubbliche attive
    List<FavoriteList> findByVisibilityAndDeletedFalse(FavoriteList.Visibility visibility);
}