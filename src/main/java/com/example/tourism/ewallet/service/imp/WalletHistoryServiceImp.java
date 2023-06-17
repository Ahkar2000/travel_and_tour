package com.example.tourism.ewallet.service.imp;

import com.example.tourism.ewallet.service.WalletHistoryService;
import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.response.WalletHistoryResponse;
import com.example.tourism.service.KeyCloakService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

@Service
@Slf4j
public class WalletHistoryServiceImp implements WalletHistoryService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    KeyCloakService keyCloakService;
    private String baseUrl = "http://localhost:8081/history";
    @Override
    public BaseResponse getAllHistory() {
        try{
            WalletHistoryResponse[] walletHistoryResponses = restTemplate.getForObject(baseUrl, WalletHistoryResponse[].class);
            return new BaseResponse("000",walletHistoryResponses);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getHistoryByUserId(Long userId, Principal principal) {
        if (!String.valueOf(userId).equals(keyCloakService.getKeycloakUserID(principal)))
            return new BaseResponse("403","You are not allowed.");
        try {
            String url = baseUrl+"/get-by-userId/{userid}";
            WalletHistoryResponse[] walletHistoryResponse = restTemplate.getForObject(url,WalletHistoryResponse[].class,userId);
            return new BaseResponse("000",walletHistoryResponse);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getHistoryByUserId(Long userId) {
        try {
            String url = baseUrl+"/get-by-userId/{userid}";
            WalletHistoryResponse[] walletHistoryResponse = restTemplate.getForObject(url,WalletHistoryResponse[].class,userId);
            return new BaseResponse("000",walletHistoryResponse);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }
}
