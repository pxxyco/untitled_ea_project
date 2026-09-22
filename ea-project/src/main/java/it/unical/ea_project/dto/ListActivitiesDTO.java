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
public class ListActivitiesDTO {

    private Long listId;

    private Long activityId;

    private LocalDateTime addedAt;
}
