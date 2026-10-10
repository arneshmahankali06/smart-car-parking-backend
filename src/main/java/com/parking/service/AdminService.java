package com.parking.service;

import com.parking.dto.AdminStatsResponse;
import com.parking.entity.SlotStatus;
import com.parking.repository.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {

    private final ParkingSlotRepository slotRepo;
    private final BookingRepository bookingRepo;
    private final PaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final ParkingHistoryRepository historyRepo;

    public AdminService(ParkingSlotRepository slotRepo,
                        BookingRepository bookingRepo,
                        PaymentRepository paymentRepo,
                        UserRepository userRepo,
                        ParkingHistoryRepository historyRepo) {
        this.slotRepo    = slotRepo;
        this.bookingRepo = bookingRepo;
        this.paymentRepo = paymentRepo;
        this.userRepo    = userRepo;
        this.historyRepo = historyRepo;
    }

    public AdminStatsResponse getStats() {
        long total    = slotRepo.count();
        long available  = slotRepo.findByStatus(SlotStatus.AVAILABLE).size();
        long occupied   = slotRepo.findByStatus(SlotStatus.OCCUPIED).size();
        long reserved   = slotRepo.findByStatus(SlotStatus.RESERVED).size();
        long totalBook  = bookingRepo.count();
        long todayBook  = bookingRepo.countTodayBookings();
        BigDecimal totalRev = paymentRepo.sumTotalRevenue();
        BigDecimal todayRev = paymentRepo.sumTodayRevenue();
        long users      = userRepo.count();

        return new AdminStatsResponse(total, available, occupied, reserved,
                totalBook, todayBook, totalRev, todayRev, users);
    }

    public List<?> getRecentHistory() {
        return historyRepo.findAllOrderByEntryTimeDesc();
    }
}
