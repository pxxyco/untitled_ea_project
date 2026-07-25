package it.unical.ea_project.service;

import it.unical.ea_project.domain.Notification;
import it.unical.ea_project.domain.User;
import org.springframework.data.domain.Page;
import java.util.List;

public interface NotificationService {

    List<Notification> getAllNotifications(User user);

    Page<Notification> getNotificationsPaginated(User user, int page, int size);

    List<Notification> getUnreadNotifications(User user);

    long countUnread(User user);

    void markAllAsRead(User user);

    Notification createNotification(Notification notification);

}