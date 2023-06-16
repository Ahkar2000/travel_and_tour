package com.example.tourism.service;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.BookingRequest;

import java.security.Principal;

public interface BookingService {
    BaseResponse getBookings(Long userId,Long packageId,Integer pageNo, Integer pageSize, String sortDir, String sortField);
    BaseResponse createBooking(BookingRequest bookingRequest, Principal principal);
    BaseResponse getBookingById(Long userId,Long id, Principal principal);
    BaseResponse getBookingById(Long id);
    BaseResponse updateBooking(Long id, BookingRequest bookingRequest, Principal principal);
    BaseResponse cancelBooking(Long userId,Long id, Principal principal);
    BaseResponse getTotalSales();
}
