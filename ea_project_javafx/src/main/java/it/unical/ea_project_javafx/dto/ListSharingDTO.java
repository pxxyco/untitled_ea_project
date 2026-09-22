package it.unical.ea_project_javafx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListSharingDTO {

    private Long listId;

    private Long userId;

    // ListSharing.SharingPermission: VIEW, EDIT
    private String permission;

    private LocalDateTime sharedAt;
}
