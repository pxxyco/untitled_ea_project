package it.unical.ea_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

    private Long reviewId;

    private Long userId;

    private Long tripId;

    private Long activityId;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;

    private Boolean deleted;
}
