package it.unical.ea_project.service.impl;


import it.unical.ea_project.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void elimina(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment non trovato");
        }
        paymentRepository.softDeleteById(id);
    }
}