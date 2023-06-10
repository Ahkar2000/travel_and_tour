package com.example.tourism.payLoad.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {
    private Long userId;

    private Double amount;

    private LocalDateTime createdAt;
}
