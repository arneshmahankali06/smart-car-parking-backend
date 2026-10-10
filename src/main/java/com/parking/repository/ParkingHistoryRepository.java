package com.parking.repository;

import com.parking.entity.ParkingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParkingHistoryRepository extends JpaRepository<ParkingHistoryEntity, Long> {
    Optional<ParkingHistoryEntity> findByBookingId(Long bookingId);

    @Query("SELECT h FROM ParkingHistoryEntity h ORDER BY h.entryTime DESC")
    List<ParkingHistoryEntity> findAllOrderByEntryTimeDesc();
}
