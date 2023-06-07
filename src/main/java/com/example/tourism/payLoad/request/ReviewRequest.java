package com.example.tourism.payLoad.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    @Min(1)
    private Long userId;

    @Min(1)
    private Long packageId;

    @NotBlank
    private String review;

    @Min(1)
    private Double rating;
}
