package it.unical.ea_project_javafx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteListDTO {

    private Long id;

    private Long userId;

    // FavoriteList.Visibility: PRIVATE, SHARED, PUBLIC
    private String visibility;

    private boolean deleted;
}
