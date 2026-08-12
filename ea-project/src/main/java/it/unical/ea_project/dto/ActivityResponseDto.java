package it.unical.ea_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponseDto {
    private Long activityId;
    private String title;
    private String description;
    private String category;
    private String city;
    private Double price;
    private Double averageRating;
    private String imageUrl;
}