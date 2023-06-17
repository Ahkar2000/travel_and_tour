package com.example.tourism.ewallet.controller;

import com.example.tourism.ewallet.service.WalletHistoryService;
import com.example.tourism.payLoad.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

@RestController
@RequestMapping("/history")
public class WalletHistoryController {
    @Autowired
    WalletHistoryService walletHistoryService;
    @GetMapping("")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> getAllHistory(){
        return ResponseEntity.ok(walletHistoryService.getAllHistory());
    }

    @GetMapping("/get-by-userId/{userId}")
    @RolesAllowed({"admin","user"})
    public ResponseEntity<BaseResponse> getHistoryByUserId(@PathVariable("userId") Long userId, Principal principal){
        return ResponseEntity.ok(walletHistoryService.getHistoryByUserId(userId,principal));
    }

    @GetMapping("/admin/get-by-userId/{userId}")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> getHistoryByUserId(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(walletHistoryService.getHistoryByUserId(userId));
    }
}
