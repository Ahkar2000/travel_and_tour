package com.example.tourism.payLoad.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PackageRequest {
    @NotBlank
    private String packageName;
    @NotBlank
    private String description;
    @Min(1)
    private Integer groupSize;
    @Min(1)
    private Integer duration;
    @Min(1)
    private Integer places;
    @NotBlank
    private String transportation;
    @Min(1)
    private Double price;
    @Min(1)
    public Long categoryId;
}
