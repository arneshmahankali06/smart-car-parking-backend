package com.parking.service;

import com.parking.entity.*;
import com.parking.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class GateService {

    // ₹30 per hour (₹0.50 per minute)
    private static final BigDecimal RATE_PER_MINUTE =
        new BigDecimal("0.50");

    private final BookingRepository bookingRepo;
    private final ParkingSlotRepository slotRepo;
    private final ParkingHistoryRepository historyRepo;

    public GateService(BookingRepository bookingRepo,
                       ParkingSlotRepository slotRepo,
                       ParkingHistoryRepository historyRepo) {
        this.bookingRepo = bookingRepo;
        this.slotRepo    = slotRepo;
        this.historyRepo = historyRepo;
    }

    /**
     * Simulate vehicle entry.
     * Marks the slot as OCCUPIED and records entry time.
     */
    @Transactional
    public ParkingHistoryEntity vehicleEntry(Long bookingId) {
        BookingEntity booking = findBookingOrThrow(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Booking is cancelled. Entry not allowed.");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Booking is already completed.");
        }

        // Check existing history
        historyRepo.findByBookingId(bookingId).ifPresent(h -> {
            if (h.getEntryTime() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Vehicle has already entered for this booking.");
            }
        });

        // Mark slot as OCCUPIED
        ParkingSlotEntity slot = booking.getSlot();
        slot.setStatus(SlotStatus.OCCUPIED);
        slot.setVehicleNumber(booking.getVehicleNumber());
        slotRepo.save(slot);

        // Create parking history record
        ParkingHistoryEntity history = new ParkingHistoryEntity();
        history.setBooking(booking);
        history.setVehicleNumber(booking.getVehicleNumber());
        history.setEntryTime(LocalDateTime.now());

        return historyRepo.save(history);
    }

    /**
     * Simulate vehicle exit.
     * Calculates fee, marks slot as AVAILABLE, and records exit time.
     */
    @Transactional
    public ParkingHistoryEntity vehicleExit(Long bookingId) {
        BookingEntity booking = findBookingOrThrow(bookingId);

        ParkingHistoryEntity history = historyRepo.findByBookingId(bookingId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No entry record found for this booking. Cannot exit."));

        if (history.getExitTime() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Vehicle has already exited for this booking.");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        history.setExitTime(exitTime);

        long minutes = ChronoUnit.MINUTES.between(history.getEntryTime(), exitTime);
        if (minutes < 1) minutes = 1; // minimum 1 minute
        history.setDurationMinutes(minutes);

        BigDecimal fee = RATE_PER_MINUTE
            .multiply(BigDecimal.valueOf(minutes))
            .setScale(2, RoundingMode.HALF_UP);
        history.setFee(fee);

        // Free the slot
        ParkingSlotEntity slot = booking.getSlot();
        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setVehicleNumber(null);
        slotRepo.save(slot);

        // Mark booking as COMPLETED
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepo.save(booking);

        return historyRepo.save(history);
    }

    public ParkingHistoryEntity getHistory(Long bookingId) {
        return historyRepo.findByBookingId(bookingId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No parking history for booking " + bookingId));
    }

    private BookingEntity findBookingOrThrow(Long bookingId) {
        return bookingRepo.findById(bookingId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Booking not found: " + bookingId));
    }
}
