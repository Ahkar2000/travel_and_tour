package com.example.tourism.ewallet.service;

import com.example.tourism.payLoad.request.WalletRequest;
import com.example.tourism.payLoad.response.BaseResponse;

import java.security.Principal;

public interface WalletService {
    BaseResponse getWalletList();
    BaseResponse createWallet(WalletRequest walletRequest, Principal principal);
    BaseResponse getById(Long id,Principal principal);
    BaseResponse getByUserId(Long userId,Principal principal);
    BaseResponse cashIn(Long id,WalletRequest walletRequest,Principal principal);
    BaseResponse cashOut(Long id,WalletRequest walletRequest,Principal principal);
}
