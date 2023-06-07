package com.example.tourism.repository;

import com.example.tourism.entity.Booking;
import com.example.tourism.projection.TotalSalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query(value = "SELECT * FROM booking AS b WHERE (:userId IS NULL OR b.user_id=:userId) AND (:packageId IS NULL OR b.package_id=:packageId) ORDER BY b.id",
    countQuery = "SELECT COUNT(*) FROM booking AS b WHERE (:userId IS NULL OR b.user_id=:userId) AND (:packageId IS NULL OR b.package_id=:packageId) ORDER BY b.id",nativeQuery = true)
    Page<Booking> getBookings(@RequestParam("userId") Long userId, @RequestParam("packageId") Long packageId, Pageable pageable);

    @Query(value = "SELECT SUM(total_price) AS totalPrice, COUNT(*) AS totalBookings FROM booking",nativeQuery = true)
    TotalSalesProjection getTotalSales();

    @Query(value = "SELECT * FROM booking AS b WHERE user_id=:userId AND package_id=:packageId",nativeQuery = true)
    List<Booking> isCustomer(Long userId,Long packageId);
}
