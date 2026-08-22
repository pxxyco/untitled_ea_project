package it.unical.ea_project.repository;

import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByCreatedBy(User createdBy);

    List<Activity> findByDeletedAtIsNull();

    Optional<Activity> findByActivityIdAndDeletedAtIsNull(Long activityId);


    List<Activity> findByDeletedAtIsNullOrderByAverageRatingDesc(Pageable pageable);

    List<Activity> findByDeletedAtIsNullAndCategory(Activity.Category category);

}