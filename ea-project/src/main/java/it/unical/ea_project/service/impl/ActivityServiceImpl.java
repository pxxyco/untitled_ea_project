package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.repository.ActivityRepository;
import it.unical.ea_project.repository.UserRepository;
import it.unical.ea_project.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findByActivityIdAndDeletedAtIsNull(id);
    }

    @Override
    @Transactional
    public Activity createActivity(Activity activity, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Utente organizzatore non trovato con ID: " + creatorId));
        activity.setCreatedBy(creator);
        return activityRepository.save(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> getActivitiesByCategory(Activity.Category category) {
        return activityRepository.findByDeletedAtIsNullAndCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> getTopActivities(int page,int size) {
        return activityRepository.findByDeletedAtIsNullOrderByAverageRatingDesc(PageRequest.of(page, size));
    }

    @Override
    @Transactional
    public Activity updateActivity(Long id, Activity details) {
        Optional<Activity> existingOpt = getActivityById(id);
        if (existingOpt.isEmpty()) {
            throw new RuntimeException("Attività non trovata con ID: " + id);
        }

        Activity existing = existingOpt.get();
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
        Optional<Activity> existingOpt = getActivityById(id);
        if (existingOpt.isPresent()) {
            Activity existing = existingOpt.get();
            existing.setDeletedAt(LocalDateTime.now());
            activityRepository.save(existing);
        }
    }
}