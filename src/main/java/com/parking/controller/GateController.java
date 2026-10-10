package com.parking.controller;

import com.parking.entity.ParkingHistoryEntity;
import com.parking.service.GateService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gate")
public class GateController {

    private final GateService gateService;

    public GateController(GateService gateService) {
        this.gateService = gateService;
    }

    /** Simulate vehicle entry: open gate, mark slot OCCUPIED */
    @PostMapping("/entry/{bookingId}")
    public ResponseEntity<ParkingHistoryEntity> vehicleEntry(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(gateService.vehicleEntry(bookingId));
    }

    /** Simulate vehicle exit: close gate, calculate fee, free slot */
    @PostMapping("/exit/{bookingId}")
    public ResponseEntity<ParkingHistoryEntity> vehicleExit(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(gateService.vehicleExit(bookingId));
    }

    /** Get parking history for a booking */
    @GetMapping("/history/{bookingId}")
    public ResponseEntity<ParkingHistoryEntity> getHistory(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(gateService.getHistory(bookingId));
    }
}
