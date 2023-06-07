package com.example.tourism.payLoad.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    String errorCode;
    Object result;
    LocalDateTime time;

    public BaseResponse(String errorCode, Object result) {
        this.errorCode = errorCode;
        this.result = result;
        this.time = LocalDateTime.now();
    }
}
