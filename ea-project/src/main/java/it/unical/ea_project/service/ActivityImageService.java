package it.unical.ea_project.service;

import it.unical.ea_project.domain.ActivityImage;
import java.util.List;

public interface ActivityImageService {
    List<ActivityImage> getImagesByActivityId(Long activityId);
    ActivityImage addImageToActivity(Long activityId, ActivityImage image);
    void deleteImage(Long imageId);
}