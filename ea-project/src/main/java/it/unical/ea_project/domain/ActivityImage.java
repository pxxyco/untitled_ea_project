package it.unical.ea_project.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "\"ACTIVITY_IMAGE\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "order_index")
    private Integer orderIndex;
}