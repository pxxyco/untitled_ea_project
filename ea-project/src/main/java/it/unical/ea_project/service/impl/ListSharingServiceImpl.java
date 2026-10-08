package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.FavoriteList;
import it.unical.ea_project.domain.ListSharing;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.repository.FavoriteListRepository;
import it.unical.ea_project.repository.ListSharingRepository;
import it.unical.ea_project.repository.UserRepository;
import it.unical.ea_project.service.ListSharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListSharingServiceImpl implements ListSharingService {

    private final ListSharingRepository listSharingRepository;
    private final FavoriteListRepository favoriteListRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ListSharing shareList(Long listId, Long userId, ListSharing.SharingPermission permission) {
        FavoriteList favoriteList = favoriteListRepository.findById(listId)
                .filter(list -> !list.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Lista non trovata o eliminata con ID: " + listId));

        User user = userRepository.findById(userId) //fixme Controllare user
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + userId));

        if (listSharingRepository.existsByFavoriteList_IdAndUser_Id(listId, userId)) {
            throw new IllegalStateException("La lista è già condivisa con questo utente. Usa l'aggiornamento dei permessi.");
        }

        ListSharing listSharing = new ListSharing();
        listSharing.setFavoriteList(favoriteList);
        listSharing.setUser(user);
        listSharing.setPermission(permission != null ? permission : ListSharing.SharingPermission.VIEW);
        listSharing.setSharedAt(LocalDateTime.now());

        return listSharingRepository.save(listSharing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListSharing> getSharingsByListId(Long listId) {
        if (!favoriteListRepository.existsById(listId)) {
            throw new IllegalArgumentException("Lista non trovata con ID: " + listId);
        }
        return listSharingRepository.findByFavoriteListId(listId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListSharing> getSharedListsForUser(Long userId) {
        if (!userRepository.existsById(userId)) { // fixme Controllare User
            throw new IllegalArgumentException("Utente non trovato con ID: " + userId);
        }
        return listSharingRepository.findByUser_Id(userId);
    }

    @Override
    @Transactional
    public void updatePermission(Long listId, Long userId, ListSharing.SharingPermission newPermission) {
        ListSharing.ListSharingId id = new ListSharing.ListSharingId(listId, userId);
        ListSharing sharing = listSharingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nessuna condivisione esistente per questa coppia lista-utente."));

        sharing.setPermission(newPermission);
        listSharingRepository.save(sharing);
    }

    @Override
    @Transactional
    public void revokeSharing(Long listId, Long userId) {
        ListSharing.ListSharingId id = new ListSharing.ListSharingId(listId, userId);
        ListSharing sharing = listSharingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nessuna condivisione esistente da revocare."));

        listSharingRepository.delete(sharing);
    }
}