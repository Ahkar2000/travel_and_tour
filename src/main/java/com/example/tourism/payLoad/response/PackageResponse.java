package com.example.tourism.payLoad.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PackageResponse {
    private String packageName;
    private String description;
    private Integer groupSize;
    private Integer duration;
    private Integer places;
    private String transportation;
    private Double price;
    public Long categoryId;
    private LocalDateTime createdAt;
}
