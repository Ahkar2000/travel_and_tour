package com.example.tourism.repository;

import com.example.tourism.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<User,Long> {

}
