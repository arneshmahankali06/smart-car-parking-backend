package com.parking.controller;

import com.parking.entity.ParkingSlotEntity;
import com.parking.entity.SlotStatus;
import com.parking.service.ParkingSlotService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class ParkingSlotController {

    private final ParkingSlotService service;

    public ParkingSlotController(ParkingSlotService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParkingSlotEntity> getAllSlots() {
        return service.getAllSlots();
    }

    @GetMapping("/available")
    public List<ParkingSlotEntity> getAvailableSlots() {
        return service.getAvailableSlots();
    }

    @GetMapping("/{id}")
    public ParkingSlotEntity getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<ParkingSlotEntity> createSlot(
            @RequestBody ParkingSlotEntity slot) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createSlot(slot));
    }

    @PutMapping("/{id}/status")
    public ParkingSlotEntity updateStatus(
            @PathVariable Long id,
            @RequestParam SlotStatus status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) {
        service.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }
}
