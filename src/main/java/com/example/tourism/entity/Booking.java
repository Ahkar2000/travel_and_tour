package com.example.tourism.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booking")
public class Booking {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "package_id")
    private Long packageId;
    @Column(name = "group_size")
    private Integer groupSize;
    @Column(name = "total_price")
    private Double totalPrice;
    @Column(name = "schedule")
    private LocalDate schedule;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public Booking(Long userId, Long packageId, Integer groupSize, Double totalPrice, LocalDate schedule) {
        this.userId = userId;
        this.packageId = packageId;
        this.groupSize = groupSize;
        this.totalPrice = totalPrice;
        this.schedule = schedule;
    }
}
