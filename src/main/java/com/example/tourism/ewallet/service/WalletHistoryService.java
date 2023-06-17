package com.example.tourism.ewallet.service;

import com.example.tourism.payLoad.response.BaseResponse;

import java.security.Principal;

public interface WalletHistoryService {
    BaseResponse getAllHistory();
    BaseResponse getHistoryByUserId(Long userId, Principal principal);
    BaseResponse getHistoryByUserId(Long userId);
}
