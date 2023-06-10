package com.example.tourism.ewallet.service.imp;

import com.example.tourism.ewallet.service.WalletService;
import com.example.tourism.payLoad.request.WalletRequest;
import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.response.WalletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class WalletServiceImp implements WalletService {
    private String baseUrl = "http://localhost:8081/wallet";
    @Autowired
    RestTemplate restTemplate;
    @Override
    public BaseResponse getWalletList() {
        WalletResponse[] walletResponses = restTemplate.getForObject(baseUrl, WalletResponse[].class);
        return new BaseResponse("000",walletResponses);
    }
    public BaseResponse createWallet(WalletRequest walletRequest){
        WalletResponse walletResponse = restTemplate.postForObject(baseUrl,walletRequest, WalletResponse.class);
        if (walletResponse != null) return null;
        return null;
    }
}
