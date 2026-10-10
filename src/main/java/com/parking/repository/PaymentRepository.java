package com.parking.repository;

import com.parking.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByBookingId(Long bookingId);
    List<PaymentEntity> findByBookingUserIdOrderByIdDesc(Long userId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.status = 'PAID'")
    BigDecimal sumTotalRevenue();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.status = 'PAID' AND DATE(p.paidAt) = CURRENT_DATE")
    BigDecimal sumTodayRevenue();
}
