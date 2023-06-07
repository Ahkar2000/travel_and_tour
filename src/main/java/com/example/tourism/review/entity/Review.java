package com.example.tourism.review.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "review")
public class Review {
    private Long id;
    private Long userId;
    private Long packageId;
    private String review;
    private Double rating;
    private LocalDateTime createdAt;
}
