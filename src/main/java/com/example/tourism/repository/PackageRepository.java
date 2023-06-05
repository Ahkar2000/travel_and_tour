package com.example.tourism.repository;

import com.example.tourism.entity.Package;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package,Long> {
    Optional<Package> findByPackageName(String packageName);

    @Query(value = "SELECT * FROM package As p WHERE (:categoryId is null or p.category_id=:categoryId) ORDER BY id",
            countQuery = "SELECT COUNT(*) FROM package As p WHERE (:categoryId is null or p.category_id=:categoryId) ORDER BY id",nativeQuery = true)
    Page<Package> getPackagesByCategory(@Param(value = "categoryId") Long categoryId, Pageable pageable);
}
