package it.unical.ea_project.repository;


import it.unical.ea_project.domain.Payment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    @Query("SELECT p FROM Payment p WHERE p.booking.user.id = :userId")
    public List<Payment> findAllByUserId(Long id);

    public Optional<Payment> findByBookingBookingId(Long bookingId);

    public Optional<Payment> findByTransactionId(String transactionId);

    @Transactional
    @Modifying
    @Query("UPDATE Payment p SET p.deletedAt = CURRENT_TIMESTAMP WHERE p.paymentId = :id")
    public void softDeleteById(Long id);

}
