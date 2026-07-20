package it.unical.ea_project.domain;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "favoriteList")

@Entity
@IdClass(ListTrips.FavoriteListTripId.class)
@Table(name = "\"LIST_TRIPS\"")
public class ListTrips {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private FavoriteList favoriteList;

    @Id
    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteListTripId implements Serializable {
        private Long favoriteList;
        private Long tripId;
    }
}
