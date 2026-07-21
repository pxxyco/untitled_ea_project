package it.unical.ea_project.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"BOOKING\"")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private long bookingId;


    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name="activity_id", nullable = false)
    private Activity activity;

    @Column(name = "seats")
    private Integer seats;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private BookingType bookingType;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private BookingStatus bookingStatus;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // -- enum per il tipo di prenotazione --
    public enum BookingType{
        TRIP, ACTIVITY
    }

    // -- enum per lo stato della prenotazione --
    public enum BookingStatus{
        PENDING, COMPLETED, CONFIRMED, CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        validateTripOrActivity();
    }

    @PreUpdate
    protected void onUpdate() {
        validateTripOrActivity();
    }

    private void validateTripOrActivity() {
        if (this.trip == null && this.activity == null) {
            throw new IllegalStateException("Una prenotazione deve essere associata a un Trip O a una Activity.");
        }
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
