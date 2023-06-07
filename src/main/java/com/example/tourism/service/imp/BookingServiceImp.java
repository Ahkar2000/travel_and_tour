package com.example.tourism.service.imp;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.entity.Booking;
import com.example.tourism.entity.Package;
import com.example.tourism.payLoad.BaseBusiness;
import com.example.tourism.payLoad.request.BookingRequest;
import com.example.tourism.payLoad.response.BookingResponse;
import com.example.tourism.repository.BookingRepository;
import com.example.tourism.repository.PackageRepository;
import com.example.tourism.repository.UserRepository;
import com.example.tourism.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookingServiceImp extends BaseBusiness implements BookingService {
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public BaseResponse getBookings(Long userId,Long packageId,Integer pageNo, Integer pageSize, String sortDir, String sortField) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<BookingResponse> bookingResponses = bookingRepository.getBookings(userId,packageId, pageable).map(this::convertBookingResponse);
        return new BaseResponse("00000", bookingResponses);
    }

    @Override
    public BaseResponse createBooking(BookingRequest bookingRequest) {
        if(packageExists(bookingRequest.getPackageId()) == null) return new BaseResponse("404","Package does not exist.");

        if(userExists(bookingRequest.getUserId()) == null) return new BaseResponse("404","User does not exist.");

        Package apackage = packageExists(bookingRequest.getPackageId());
        Booking booking = (Booking) changeBookingRequest(bookingRequest);

        if(bookingRequest.getSchedule().isBefore(LocalDate.now())) return new BaseResponse("422","Booking date is invalid.");

        if(bookingRequest.getGroupSize() > apackage.getGroupSize()) return new BaseResponse("409","Your group size is exceeding the maximum amount of package.");

        booking.setUserId(bookingRequest.getUserId());
        booking.setSchedule(bookingRequest.getSchedule());
        booking.setGroupSize(bookingRequest.getGroupSize());
        booking.setPackageId(bookingRequest.getPackageId());
        booking.setTotalPrice(apackage.getPrice());
        booking.setCreatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return new BaseResponse("000",convertBookingResponse(booking));
    }
    @Override
    public BaseResponse getTotalPrice() {
        return new BaseResponse("000",bookingRepository.getTotalSales());
    }

    @Override
    public BaseResponse getBookingById(Long id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if(bookingOptional.isEmpty()){
            return new BaseResponse("404", "Booking Not found.");
        }
        Booking booking = bookingOptional.get();
        return new BaseResponse("000",convertBookingResponse(booking));
    }

    @Override
    public BaseResponse updateBooking(Long id, BookingRequest bookingRequest) {

        if(bookingExists(id) == null) return new BaseResponse("404","Booking does not exist.");

        if(packageExists(bookingRequest.getPackageId()) == null) return new BaseResponse("404","Package does not exist.");

        if(userExists(bookingRequest.getUserId()) == null) return new BaseResponse("404","User does not exist.");

        if(bookingRequest.getSchedule().isBefore(LocalDate.now())) return new BaseResponse("422","Booking date is invalid.");

        Package apackage = packageExists(bookingRequest.getPackageId());

        if(bookingRequest.getGroupSize() > apackage.getGroupSize()) return new BaseResponse("409","Your group size is exceeding the maximum amount of package.");
        Booking booking = bookingExists(id);
        booking.setSchedule(bookingRequest.getSchedule());
        booking.setUserId(bookingRequest.getUserId());
        booking.setGroupSize(bookingRequest.getGroupSize());
        booking.setTotalPrice(apackage.getPrice());
        booking.setSchedule(bookingRequest.getSchedule());
        bookingRepository.save(booking);
        return new BaseResponse("000",convertBookingResponse(booking));
    }

    @Override
    public BaseResponse deleteById(Long id) {
        if(bookingExists(id) == null) return new BaseResponse("404", "Booking Not found.");
        bookingRepository.deleteById(id);
        return new BaseResponse("000","Booking is deleted.");
    }
    private BookingResponse convertBookingResponse(Booking booking){
        return new BookingResponse(booking.getUserId(),booking.getPackageId(),booking.getGroupSize(),booking.getTotalPrice(),booking.getSchedule(),booking.getCreatedAt());
    }
}
