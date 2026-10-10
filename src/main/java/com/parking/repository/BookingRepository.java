package com.parking.repository;

import com.parking.entity.BookingEntity;
import com.parking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<BookingEntity> findAllByOrderByCreatedAtDesc();

    /**
     * Check for overlapping bookings on the same slot.
     * A new booking [newStart, newEnd] overlaps an existing booking
     * if newStart < existingEnd AND newEnd > existingStart.
     */
    @Query("""
        SELECT COUNT(b) > 0 FROM BookingEntity b
        WHERE b.slot.id = :slotId
          AND b.status IN ('PENDING', 'CONFIRMED')
          AND b.startTime < :endTime
          AND b.endTime > :startTime
    """)
    boolean existsOverlappingBooking(
        @Param("slotId") Long slotId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * Same overlap check but excluding a specific booking id (used for updates).
     */
    @Query("""
        SELECT COUNT(b) > 0 FROM BookingEntity b
        WHERE b.slot.id = :slotId
          AND b.id <> :excludeId
          AND b.status IN ('PENDING', 'CONFIRMED')
          AND b.startTime < :endTime
          AND b.endTime > :startTime
    """)
    boolean existsOverlappingBookingExcluding(
        @Param("slotId") Long slotId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("excludeId") Long excludeId
    );

    long countByStatus(BookingStatus status);

    @Query("SELECT COUNT(b) FROM BookingEntity b WHERE DATE(b.createdAt) = CURRENT_DATE")
    long countTodayBookings();
}
