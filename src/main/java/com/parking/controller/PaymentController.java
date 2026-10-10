package com.parking.controller;

import com.parking.dto.PaymentRequest;
import com.parking.entity.PaymentEntity;
import com.parking.entity.UserEntity;
import com.parking.service.PaymentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentEntity> processPayment(
            @Valid @RequestBody PaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.processPayment(req));
    }

    @GetMapping("/booking/{bookingId}")
    public PaymentEntity getByBookingId(@PathVariable Long bookingId) {
        return service.getByBookingId(bookingId);
    }

    @GetMapping("/my")
    public List<PaymentEntity> getMyPayments(
            @AuthenticationPrincipal UserEntity currentUser) {
        return service.getUserPaymentHistory(currentUser.getId());
    }
}
