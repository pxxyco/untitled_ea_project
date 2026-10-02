package it.unical.ea_project.dto.home;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityHomeDTO {

    private Long activityId;
    private String title;
    private String city;
    private String category;
    private Double price;
    private Double averageRating;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}