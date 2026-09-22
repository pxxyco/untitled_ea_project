package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.domain.ActivityImage;
import it.unical.ea_project.repository.ActivityImageRepository;
import it.unical.ea_project.repository.ActivityRepository;
import it.unical.ea_project.service.ActivityImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityImageServiceImpl implements ActivityImageService {

    private final ActivityImageRepository activityImageRepository;
    private final ActivityRepository activityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ActivityImage> getImagesByActivityId(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Attività non trovata con ID: " + activityId));
        return activityImageRepository.findByActivityOrderByOrderIndexAsc(activity);
    }

    @Override
    @Transactional
    public ActivityImage addImageToActivity(Long activityId, ActivityImage image) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Attività non trovata con ID: " + activityId));
        image.setActivity(activity);
        return activityImageRepository.save(image);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityImage> getImagesByActivityIds(List<Long> activityIds) {

        if (activityIds == null || activityIds.isEmpty()) {
            return List.of();
        }
        return activityImageRepository.findByActivityIds(activityIds);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        activityImageRepository.deleteById(imageId);
    }
}