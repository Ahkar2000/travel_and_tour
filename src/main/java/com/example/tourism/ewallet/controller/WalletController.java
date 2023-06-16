package com.example.tourism.ewallet.controller;

import com.example.tourism.ewallet.service.imp.WalletServiceImp;
import com.example.tourism.payLoad.request.WalletRequest;
import com.example.tourism.payLoad.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    WalletServiceImp walletServiceImp;

    @GetMapping("")
    @RolesAllowed("admin")
    ResponseEntity<BaseResponse> getWalletList(){
        return ResponseEntity.ok(walletServiceImp.getWalletList());
    }

    @GetMapping("/{id}")
    @RolesAllowed("admin")
    ResponseEntity<BaseResponse> getWalletById(@PathVariable("id") Long id,Principal principal){
        return ResponseEntity.ok(walletServiceImp.getById(id,principal));
    }

    @GetMapping("/get-by-userId/{userId}")
    @RolesAllowed({"user","admin"})
    ResponseEntity<BaseResponse> getWalletByUserId(@PathVariable("userId") Long userId,Principal principal){
        return ResponseEntity.ok(walletServiceImp.getByUserId(userId,principal));
    }

    @PostMapping("/create")
    @RolesAllowed("user")
    ResponseEntity<BaseResponse> createWallet(@RequestBody WalletRequest walletRequest,Principal principal){
        return ResponseEntity.ok(walletServiceImp.createWallet(walletRequest,principal));
    }

    @PutMapping("/cash-in/{id}")
    @RolesAllowed("user")
    ResponseEntity<BaseResponse> cashIn(@RequestBody WalletRequest walletRequest,@PathVariable("id") Long id,Principal principal){
        return ResponseEntity.ok(walletServiceImp.cashIn(id,walletRequest,principal));
    }

    @PutMapping("/cash-out/{id}")
    @RolesAllowed("user")
    ResponseEntity<BaseResponse> cashOut(@RequestBody WalletRequest walletRequest,@PathVariable("id") Long id,Principal principal){
        return ResponseEntity.ok(walletServiceImp.cashOut(id,walletRequest,principal));
    }
}
