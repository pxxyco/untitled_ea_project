package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.FavoriteList;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.repositories.FavoriteListRepository;
import it.unical.ea_project.repositories.UserRepository;
import it.unical.ea_project.service.FavoriteListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteListServiceImpl implements FavoriteListService {

    private final FavoriteListRepository favoriteListRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FavoriteList createFavoriteList(Long userId, FavoriteList.Visibility visibility) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + userId));

        FavoriteList favoriteList = new FavoriteList();
        favoriteList.setUser(user);
        favoriteList.setVisibility(visibility != null ? visibility : FavoriteList.Visibility.PRIVATE);
        favoriteList.setDeleted(false);

        return favoriteListRepository.save(favoriteList);
    }


    @Override
    @Transactional(readOnly = true)
    public List<FavoriteList> getActiveListsByUser(Long userId) {
        //Controlla se l'utente esiste fixme
        //return favoriteListRepository.findByUserUserIdAndDeletedFalse(userId);
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public FavoriteList getListById(Long id) {
        return favoriteListRepository.findById(id)
                .filter(list -> !list.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Lista dei preferiti non trovata o eliminata con ID: " + id));
    }

    @Override
    @Transactional
    public void softDeleteList(Long id) {
        FavoriteList favoriteList = favoriteListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lista dei preferiti non trovata con ID: " + id));
        favoriteList.setDeleted(true);
        favoriteListRepository.save(favoriteList);
    }
}