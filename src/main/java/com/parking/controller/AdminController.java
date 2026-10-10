package com.parking.controller;

import com.parking.dto.AdminStatsResponse;
import com.parking.dto.BookingResponse;
import com.parking.service.AdminService;
import com.parking.service.BookingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final BookingService bookingService;

    public AdminController(AdminService adminService,
                           BookingService bookingService) {
        this.adminService  = adminService;
        this.bookingService = bookingService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/reports/history")
    public ResponseEntity<List<?>> getParkingHistory() {
        return ResponseEntity.ok(adminService.getRecentHistory());
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
