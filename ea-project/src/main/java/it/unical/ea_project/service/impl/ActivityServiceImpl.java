package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.security.ResourceNotFoundException;
import it.unical.ea_project.repository.ActivityRepository;
import it.unical.ea_project.repository.UserRepository;
import it.unical.ea_project.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Activity> getAllActivities() {
        return activityRepository.findByDeletedAtIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public Activity getActivityById(Long id) {
        return activityRepository.findByActivityIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attività non trovata con ID: " + id));
    }

    @Override
    @Transactional
    public Activity createActivity(Activity activity, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente organizzatore non trovato con ID: " + creatorId));
        activity.setCreatedBy(creator);
        return activityRepository.save(activity);
    }

    @Override
    @Transactional
    public Activity updateActivity(Long id, Activity details) {
        Activity existing = getActivityById(id);

        existing.setTitle(details.getTitle());
        existing.setDescription(details.getDescription());
        existing.setCategory(details.getCategory());
        existing.setLatitude(details.getLatitude());
        existing.setLongitude(details.getLongitude());
        existing.setPlaceName(details.getPlaceName());
        existing.setCity(details.getCity());
        existing.setStartDate(details.getStartDate());
        existing.setEndDate(details.getEndDate());
        existing.setDurationMinutes(details.getDurationMinutes());
        existing.setPrice(details.getPrice());
        existing.setMaxSeats(details.getMaxSeats());
        existing.setAvailableSeats(details.getAvailableSeats());
        existing.setStatus(details.getStatus());
        existing.setNotes(details.getNotes());

        return activityRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        Activity existing = getActivityById(id);
        existing.setDeletedAt(LocalDateTime.now()); // soft delete, coerente con Trip
        activityRepository.save(existing);
    }
}