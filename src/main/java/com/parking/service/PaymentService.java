package com.parking.service;

import com.parking.dto.PaymentRequest;
import com.parking.entity.*;
import com.parking.repository.BookingRepository;
import com.parking.repository.PaymentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final BookingRepository bookingRepo;

    public PaymentService(PaymentRepository paymentRepo,
                          BookingRepository bookingRepo) {
        this.paymentRepo = paymentRepo;
        this.bookingRepo = bookingRepo;
    }

    @Transactional
    public PaymentEntity processPayment(PaymentRequest req) {
        BookingEntity booking = bookingRepo.findById(req.getBookingId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Booking not found"));

        // Check for existing payment
        paymentRepo.findByBookingId(booking.getId()).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.PAID) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Payment already completed for this booking");
            }
        });

        PaymentEntity payment = paymentRepo.findByBookingId(booking.getId())
            .orElse(new PaymentEntity());

        payment.setBooking(booking);
        payment.setAmount(req.getAmount());
        payment.setMethod(req.getMethod());

        // Simulate payment gateway – in production, integrate Razorpay/Stripe here
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionRef("TXN-" + UUID.randomUUID().toString().toUpperCase());
        payment.setPaidAt(LocalDateTime.now());

        // Update booking status to COMPLETED
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepo.save(booking);

        return paymentRepo.save(payment);
    }

    public PaymentEntity getByBookingId(Long bookingId) {
        return paymentRepo.findByBookingId(bookingId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Payment not found for booking " + bookingId));
    }

    public List<PaymentEntity> getUserPaymentHistory(Long userId) {
        return paymentRepo.findByBookingUserIdOrderByIdDesc(userId);
    }
}
