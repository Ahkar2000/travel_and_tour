package com.example.tourism.payLoad.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletHistoryResponse {
    private Long userId;
    private Long walletId;
    private Double beforeAmount;
    private Double afterAmount;
    private String reason;
    private LocalDateTime createdAt;
}
