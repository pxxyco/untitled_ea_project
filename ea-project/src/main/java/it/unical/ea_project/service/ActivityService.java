package it.unical.ea_project.service;

import it.unical.ea_project.domain.Activity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ActivityService {
    List<Activity> getAllActivities();
    Optional<Activity> getActivityById(Long id);
    Activity createActivity(Activity activity, Long creatorId);
    Activity updateActivity(Long id, Activity activityDetails);
    void deleteActivity(Long id);
    List<Activity> getActivitiesByCategory(Activity.Category category);
    List<Activity> getTopActivities(int page, int size);
}