package com.example.tourism.payLoad.request;


import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {

    @Min(1)
    private Long userId;
    @Min(1)
    private Long packageId;
    @Min(1)
    private Integer groupSize;
    @NotNull
    private LocalDate schedule;

}
