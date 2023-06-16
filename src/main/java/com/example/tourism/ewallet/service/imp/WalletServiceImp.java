package com.example.tourism.ewallet.service.imp;

import com.example.tourism.ewallet.service.WalletService;
import com.example.tourism.payLoad.BaseBusiness;
import com.example.tourism.payLoad.request.WalletRequest;
import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.response.WalletResponse;
import com.example.tourism.service.KeyCloakService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

@Service
@Slf4j
public class WalletServiceImp extends BaseBusiness implements WalletService {
    @Autowired
    KeyCloakService keyCloakService;
    private String baseUrl = "http://localhost:8081/wallet";
    @Autowired
    RestTemplate restTemplate;
    @Override
    public BaseResponse getWalletList() {
        try{
            WalletResponse[] walletResponses = restTemplate.getForObject(baseUrl, WalletResponse[].class);
            return new BaseResponse("000",walletResponses);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }
    public BaseResponse createWallet(WalletRequest walletRequest, Principal principal){
        if (!String.valueOf(walletRequest.getUserId()).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try{
            if(userExists(walletRequest.getUserId()) == null) return new BaseResponse("404","User not found.");
            WalletResponse walletResponse = restTemplate.postForObject(baseUrl,walletRequest, WalletResponse.class);
            if (walletResponse == null) return new BaseResponse("500","Can't create wallet account.");
            return new BaseResponse("000",walletResponse);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getById(Long id, Principal principal) {
        try {
            String url = baseUrl+"/{id}";
            WalletResponse walletResponse = restTemplate.getForObject(url, WalletResponse.class,id);
            if (walletResponse == null) return new BaseResponse("404","Wallet account not found.");
            return new BaseResponse("000",walletResponse);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getByUserId(Long userId, Principal principal) {
        if (!String.valueOf(userId).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try {
            String url = baseUrl+"/get-by-userId/{userId}";

            WalletResponse walletResponse = restTemplate.getForObject(url, WalletResponse.class,userId,userId);
            if (walletResponse == null) return new BaseResponse("404","Wallet account not found.");
            return new BaseResponse("000",walletResponse);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse cashIn(Long id, WalletRequest walletRequest, Principal principal) {
        if (!String.valueOf(walletRequest.getUserId()).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try {
            String url = baseUrl+"/cash-in/{id}";

            ResponseEntity<WalletResponse> response = restTemplate.exchange(url,HttpMethod.PUT, new HttpEntity<>(walletRequest), WalletResponse.class,id);
            WalletResponse walletResponse = response.getBody();
            if (walletResponse == null) return new BaseResponse("404","Wallet account not found.");
            return new BaseResponse("000",walletResponse);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse cashOut(Long id, WalletRequest walletRequest, Principal principal) {
        if (!String.valueOf(walletRequest.getUserId()).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try{
            String url = baseUrl+"/cash-out/{id}";

            ResponseEntity<WalletResponse> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(walletRequest), WalletResponse.class,id);

            WalletResponse walletResponse = response.getBody();
            if (walletResponse == null) return new BaseResponse("500","Can't withdraw from wallet account.");
            return new BaseResponse("000",walletResponse);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }
}
