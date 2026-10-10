package com.parking.dto;

import java.math.BigDecimal;

public class AdminStatsResponse {
    private long totalSlots;
    private long availableSlots;
    private long occupiedSlots;
    private long reservedSlots;
    private long totalBookings;
    private long todayBookings;
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private long totalUsers;

    public AdminStatsResponse(
            long totalSlots, long availableSlots, long occupiedSlots,
            long reservedSlots, long totalBookings, long todayBookings,
            BigDecimal totalRevenue, BigDecimal todayRevenue, long totalUsers) {
        this.totalSlots     = totalSlots;
        this.availableSlots = availableSlots;
        this.occupiedSlots  = occupiedSlots;
        this.reservedSlots  = reservedSlots;
        this.totalBookings  = totalBookings;
        this.todayBookings  = todayBookings;
        this.totalRevenue   = totalRevenue;
        this.todayRevenue   = todayRevenue;
        this.totalUsers     = totalUsers;
    }

    public long getTotalSlots()      { return totalSlots; }
    public long getAvailableSlots()  { return availableSlots; }
    public long getOccupiedSlots()   { return occupiedSlots; }
    public long getReservedSlots()   { return reservedSlots; }
    public long getTotalBookings()   { return totalBookings; }
    public long getTodayBookings()   { return todayBookings; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public long getTotalUsers()      { return totalUsers; }
}
