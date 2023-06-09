package com.example.tourism.payLoad.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long userId;
    private Long packageId;
    private Integer groupSize;
    private Double totalPrice;
    private LocalDate schedule;
    private LocalDateTime createdAt;

}
