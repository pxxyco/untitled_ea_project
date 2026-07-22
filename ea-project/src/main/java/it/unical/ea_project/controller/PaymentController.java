package it.unical.ea_project.controller;


import it.unical.ea_project.repository.PaymentRepository;
import it.unical.ea_project.service.impl.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

}
