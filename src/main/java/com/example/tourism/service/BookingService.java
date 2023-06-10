package com.example.tourism.service;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.BookingRequest;

public interface BookingService {
    BaseResponse getBookings(Long userId,Long packageId,Integer pageNo, Integer pageSize, String sortDir, String sortField);
    BaseResponse createBooking(BookingRequest bookingRequest);
    BaseResponse getBookingById(Long userId,Long id);
    BaseResponse updateBooking(Long id, BookingRequest bookingRequest);
    BaseResponse deleteById(Long userId,Long id);
    BaseResponse getTotalPrice();
}
