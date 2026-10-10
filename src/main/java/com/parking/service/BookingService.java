package com.parking.service;

import com.parking.dto.BookingRequest;
import com.parking.dto.BookingResponse;
import com.parking.entity.*;
import com.parking.repository.BookingRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final ParkingSlotRepository slotRepo;
    private final UserRepository userRepo;

    public BookingService(BookingRepository bookingRepo,
                          ParkingSlotRepository slotRepo,
                          UserRepository userRepo) {
        this.bookingRepo = bookingRepo;
        this.slotRepo    = slotRepo;
        this.userRepo    = userRepo;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest req, Long userId) {
        // Validate times
        if (!req.getEndTime().isAfter(req.getStartTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End time must be after start time");
        }

        UserEntity user = userRepo.findById(userId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ParkingSlotEntity slot = slotRepo.findById(req.getSlotId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Slot not found"));

        // Check for overlapping bookings (with pessimistic lock via transaction)
        boolean overlap = bookingRepo.existsOverlappingBooking(
            slot.getId(), req.getStartTime(), req.getEndTime()
        );
        if (overlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Slot is already booked for the selected time period");
        }

        // Check current slot status
        if (slot.getStatus() == SlotStatus.OCCUPIED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Slot is currently occupied");
        }

        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setSlot(slot);
        booking.setStartTime(req.getStartTime());
        booking.setEndTime(req.getEndTime());
        booking.setVehicleNumber(req.getVehicleNumber());
        booking.setStatus(BookingStatus.CONFIRMED);

        // Generate a QR code string (encode booking details)
        String qr = "BOOKING:" + UUID.randomUUID() +
                    "|SLOT:" + slot.getSlotNumber() +
                    "|USER:" + user.getName() +
                    "|VEH:" + req.getVehicleNumber();
        booking.setQrCode(qr);

        // Mark slot as RESERVED
        slot.setStatus(SlotStatus.RESERVED);
        slotRepo.save(slot);

        return BookingResponse.from(bookingRepo.save(booking));
    }

    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepo.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(BookingResponse::from)
            .collect(Collectors.toList());
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepo.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(BookingResponse::from)
            .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long id) {
        return BookingResponse.from(findOrThrow(id));
    }

    @Transactional
    public BookingResponse cancelBooking(Long id, Long userId) {
        BookingEntity booking = findOrThrow(id);

        // Only the owner or admin can cancel
        if (!booking.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only cancel your own bookings");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel a completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        // Free the slot
        ParkingSlotEntity slot = booking.getSlot();
        if (slot.getStatus() == SlotStatus.RESERVED) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepo.save(slot);
        }

        return BookingResponse.from(bookingRepo.save(booking));
    }

    @Transactional
    public BookingResponse adminCancelBooking(Long id) {
        BookingEntity booking = findOrThrow(id);
        booking.setStatus(BookingStatus.CANCELLED);
        ParkingSlotEntity slot = booking.getSlot();
        if (slot.getStatus() == SlotStatus.RESERVED) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepo.save(slot);
        }
        return BookingResponse.from(bookingRepo.save(booking));
    }

    private BookingEntity findOrThrow(Long id) {
        return bookingRepo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }
}
