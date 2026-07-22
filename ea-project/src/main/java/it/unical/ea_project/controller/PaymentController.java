package it.unical.ea_project.controller;


import it.unical.ea_project.service.impl.PaymentServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentServiceImpl paymentService;
    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

}
