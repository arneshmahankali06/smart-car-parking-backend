package com.parking.controller;

import com.parking.dto.BookingRequest;
import com.parking.dto.BookingResponse;
import com.parking.entity.UserEntity;
import com.parking.service.BookingService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    /** Create a booking — user ID is taken from the JWT, not the request body */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest req,
            @AuthenticationPrincipal UserEntity currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createBooking(req, currentUser.getId()));
    }

    /** Get the current user's bookings */
    @GetMapping("/my")
    public List<BookingResponse> getMyBookings(
            @AuthenticationPrincipal UserEntity currentUser) {
        return service.getUserBookings(currentUser.getId());
    }

    /** Get a specific booking */
    @GetMapping("/{id}")
    public BookingResponse getById(@PathVariable Long id) {
        return service.getBookingById(id);
    }

    /** Cancel own booking */
    @PutMapping("/{id}/cancel")
    public BookingResponse cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity currentUser) {
        return service.cancelBooking(id, currentUser.getId());
    }

    /** Admin: get all bookings */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponse> getAllBookings() {
        return service.getAllBookings();
    }

    /** Admin: cancel any booking */
    @PutMapping("/{id}/admin-cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public BookingResponse adminCancelBooking(@PathVariable Long id) {
        return service.adminCancelBooking(id);
    }
}
