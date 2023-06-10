package com.example.tourism.ewallet.controller;

import com.example.tourism.ewallet.service.imp.WalletServiceImp;
import com.example.tourism.payLoad.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    WalletServiceImp walletServiceImp;

    @GetMapping("")
    ResponseEntity<BaseResponse> getWalletList(){
        return ResponseEntity.ok(walletServiceImp.getWalletList());
    }
}
