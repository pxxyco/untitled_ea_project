package it.unical.ea_project.repository;


import it.unical.ea_project.domain.Review;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SQLRestriction("deleted_at = false")
@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    List<Review> findAllByUserId(Long userId);

    List<Review> findAllByTripTripIdOrderByCreatedAtDesc(Long tripId);

    List<Review> findAllByActivityActivityIdOrderByCreatedAtDesc(Long activityId);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.deleted = true WHERE r.reviewId = :id")
    void softDeleteById(Long id);
}
