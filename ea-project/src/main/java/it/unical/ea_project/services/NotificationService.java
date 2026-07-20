package it.unical.ea_project.services;

import it.unical.ea_project.domain.Notification;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.repositories.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Page<Notification> getNotificationsPaginated(User user, int page, int size) {
        return notificationRepository.findByUser(user, PageRequest.of(page, size));
    }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndReadOrderByCreatedAtDesc(user, false);
    }

    public long countUnread(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadForUser(user);
    }

    @Transactional
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
}
