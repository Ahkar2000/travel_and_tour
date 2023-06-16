package com.example.tourism.service.imp;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.entity.Booking;
import com.example.tourism.entity.Package;
import com.example.tourism.payLoad.BaseBusiness;
import com.example.tourism.payLoad.request.BookingRequest;
import com.example.tourism.payLoad.response.BookingResponse;
import com.example.tourism.payLoad.response.WalletResponse;
import com.example.tourism.projection.TotalSalesProjection;
import com.example.tourism.repository.BookingRepository;
import com.example.tourism.repository.PackageRepository;
import com.example.tourism.repository.UserRepository;
import com.example.tourism.service.BookingService;
import com.example.tourism.service.KeyCloakService;
import com.example.tourism.service.kafka.KafkaMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class BookingServiceImp extends BaseBusiness implements BookingService {
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    KeyCloakService keyCloakService;

    @Override
    public BaseResponse getBookings(Long userId,Long packageId,Integer pageNo, Integer pageSize, String sortDir, String sortField) {
        try{
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
            Page<BookingResponse> bookingResponses = bookingRepository.getBookings(userId,packageId, pageable).map(this::convertBookingResponse);
            return new BaseResponse("00000", bookingResponses);
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse createBooking(BookingRequest bookingRequest, Principal principal) {
        if (!String.valueOf(bookingRequest.getUserId()).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try{
            Map<String, Double> result = validateBooking(bookingRequest);
            if (result.containsKey("error")) {
                String errorMessage = String.valueOf(result.get("error"));
                return new BaseResponse("400",errorMessage);
            }
            Double userAmount = result.get("userAmount");
            Double packagePrice = result.get("packagePrice");

            Booking booking = (Booking) changeBookingRequest(bookingRequest);

            booking.setUserId(bookingRequest.getUserId());
            booking.setSchedule(bookingRequest.getSchedule());
            booking.setGroupSize(bookingRequest.getGroupSize());
            booking.setPackageId(bookingRequest.getPackageId());
            booking.setTotalPrice(packagePrice);
            booking.setCreatedAt(LocalDateTime.now());

            //update user wallet
            updateUserWallet(bookingRequest.getUserId(),userAmount - packagePrice);

            //send mail to user
            Booking savedBooking = bookingRepository.save(booking);
            kafkaMessageSender.sendMessage("tourism-topic",null,String.valueOf(savedBooking.getId()));

            return new BaseResponse("000",convertBookingResponse(booking));
        }catch(Exception e){
            log.info("error : "+e);
            return new BaseResponse("001",e.getMessage());
        }
    }
    @Override
    public BaseResponse getTotalSales() {
        try{
            TotalSalesProjection totalSales = bookingRepository.getTotalSales();
            if(totalSales.getTotalBookings() == 0) return new BaseResponse("404","No sales yet.");
            return new BaseResponse("000",bookingRepository.getTotalSales());
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse getBookingById(Long userId,Long id, Principal principal) {
        if (!String.valueOf(userId).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try{
            Booking booking = bookingExists(id);
            if(booking == null) return new BaseResponse("404", "Booking Not found.");
            return new BaseResponse("000",convertBookingResponse(booking));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }
    public BaseResponse getBookingById(Long id) {
        try {
            Booking booking = bookingExists(id);
            if(booking == null) return new BaseResponse("404", "Booking Not found.");
            return new BaseResponse("000",convertBookingResponse(booking));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }
    @Override
    public BaseResponse updateBooking(Long id,BookingRequest bookingRequest, Principal principal) {
        if (!String.valueOf(bookingRequest.getUserId()).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try {
            Booking booking = bookingExists(id);
            Map<String, Double> result = validateBooking(bookingRequest);
            if (result.containsKey("error")) {
                String errorMessage = String.valueOf(result.get("error"));
                return new BaseResponse("400",errorMessage);
            }
            Double userAmount = result.get("userAmount");
            Double packagePrice = result.get("packagePrice");
            userAmount = userAmount + booking.getTotalPrice();

            booking.setSchedule(bookingRequest.getSchedule());
            booking.setUserId(bookingRequest.getUserId());
            booking.setGroupSize(bookingRequest.getGroupSize());
            booking.setTotalPrice(packagePrice);
            booking.setSchedule(bookingRequest.getSchedule());
            bookingRepository.save(booking);

            //send mail to user
            Booking savedBooking = bookingRepository.save(booking);
            kafkaMessageSender.sendMessage("tourism-topic",null,String.valueOf(savedBooking.getId()));

            //update user wallet
            updateUserWallet(bookingRequest.getUserId(),userAmount - packagePrice);

            return new BaseResponse("000", convertBookingResponse(booking));
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }

    @Override
    public BaseResponse cancelBooking(Long userId,Long id, Principal principal) {
        if (!String.valueOf(userId).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try {
            Booking booking = bookingExists(id);
            if(booking == null) return new BaseResponse("404", "Booking Not found.");
            updateUserWallet(userId,booking.getTotalPrice() + getUserAmount(userId));
            bookingRepository.deleteById(id);
            return new BaseResponse("000","Booking is cancelled.");
        }catch(Exception e){
            log.info("error : "+e.getMessage());
            return new BaseResponse("001",e.getMessage());
        }
    }
    private BookingResponse convertBookingResponse(Booking booking){
        return new BookingResponse(booking.getUserId(),booking.getPackageId(),booking.getGroupSize(),booking.getTotalPrice(),booking.getSchedule(),booking.getCreatedAt());
    }
    private Double getUserAmount(Long userId){
        String url = "http://localhost:8081/wallet/get-by-userId/{userId}";
        WalletResponse walletResponse = restTemplate.getForObject(url, WalletResponse.class,userId);
        if(walletResponse == null){
            return null;
        }
        return walletResponse.getAmount();
    }
    private void updateUserWallet(Long userId,Double amount){
        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setUserId(userId);
        walletResponse.setAmount(amount);
        String url = "http://localhost:8081/wallet/update";
        restTemplate.put(url,walletResponse);
        log.info("Wallet account is updated.");
    }
    private <T> Map<String, T> validateBooking(BookingRequest bookingRequest) {
        Package apackage = packageExists(bookingRequest.getPackageId());
        Double userAmount = getUserAmount(bookingRequest.getUserId());
        Map<String, T> result = new HashMap<>();
        if (userAmount == null) {
            result.put("error", (T) "You do not have wallet account.");
        } else if (apackage == null) {
            result.put("error", (T) "Package does not exist.");
        } else if (bookingRequest.getSchedule().isBefore(LocalDate.now())) {
            result.put("error", (T) "Booking date is invalid.");
        } else if (bookingRequest.getGroupSize() > apackage.getGroupSize()) {
            result.put("error", (T) "Your group size is exceeding the maximum amount of the package.");
        } else if (apackage.getPrice() > userAmount) {
            result.put("error", (T) "You don't have enough balance.");
        } else {
            result.put("userAmount", (T) userAmount);
            result.put("packagePrice", (T) apackage.getPrice());
        }
        return result;
    }
}
