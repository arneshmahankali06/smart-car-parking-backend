package com.parking.service;

import com.parking.entity.ParkingSlotEntity;
import com.parking.entity.SlotStatus;
import com.parking.repository.ParkingSlotRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ParkingSlotService {

    private final ParkingSlotRepository repo;

    public ParkingSlotService(ParkingSlotRepository repo) {
        this.repo = repo;
    }

    public List<ParkingSlotEntity> getAllSlots() {
        return repo.findAll();
    }

    public List<ParkingSlotEntity> getAvailableSlots() {
        return repo.findByStatus(SlotStatus.AVAILABLE);
    }

    public ParkingSlotEntity createSlot(ParkingSlotEntity slot) {
        if (slot.getSlotNumber() == null || slot.getSlotNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Slot number is required");
        }
        if (repo.existsBySlotNumber(slot.getSlotNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Slot number already exists: " + slot.getSlotNumber());
        }
        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setVehicleNumber(null);
        return repo.save(slot);
    }

    public ParkingSlotEntity updateStatus(Long id, SlotStatus status) {
        ParkingSlotEntity slot = repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));
        slot.setStatus(status);
        if (status == SlotStatus.AVAILABLE) {
            slot.setVehicleNumber(null);
        }
        return repo.save(slot);
    }

    public ParkingSlotEntity getById(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));
    }

    public void deleteSlot(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found");
        }
        repo.deleteById(id);
    }
}
