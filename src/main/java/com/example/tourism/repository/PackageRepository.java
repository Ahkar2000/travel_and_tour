package com.example.tourism.repository;

import com.example.tourism.entity.Package;
import com.example.tourism.payLoad.response.PopularPackagesResponse;
import com.example.tourism.projection.PopularPackageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package,Long> {
    Optional<Package> findByPackageName(String packageName);

    @Query(value = "SELECT * FROM package AS p WHERE (:categoryId is null or p.category_id=:categoryId) ORDER BY p.id",
            countQuery = "SELECT COUNT(*) FROM package As p WHERE (:categoryId is null or p.category_id=:categoryId) ORDER BY p.id",nativeQuery = true)
    Page<Package> getPackagesByCategory(@Param(value = "categoryId") Long categoryId, Pageable pageable);

    @Query(value = "SELECT p.package_name packageName, COUNT(*) bookingCount FROM booking b JOIN package p ON b.package_id = p.id GROUP BY p.id ORDER BY bookingCount DESC",nativeQuery = true)
    List<PopularPackageProjection> findPopularPackages();
}
