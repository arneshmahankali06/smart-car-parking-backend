package com.parking.repository;

import com.parking.entity.ParkingSlotEntity;
import com.parking.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlotEntity, Long> {
    List<ParkingSlotEntity> findByStatus(SlotStatus status);
    boolean existsBySlotNumber(String slotNumber);
}
