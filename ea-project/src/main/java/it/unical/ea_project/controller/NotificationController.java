package it.unical.ea_project.controller;

import org.springframework.web.bind.annotation.*;

import it.unical.ea_project.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

}
