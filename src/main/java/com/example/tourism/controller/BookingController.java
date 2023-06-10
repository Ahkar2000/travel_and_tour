package com.example.tourism.controller;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.BookingRequest;
import com.example.tourism.service.imp.BookingServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    BookingServiceImp bookingServiceImp;

    @GetMapping("")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> getBookings(
            @RequestParam(name = "userId",required = false) Long userId,
            @RequestParam(name = "packageId",required = false) Long packageId,
            @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortDir",defaultValue = "ASC") String sortDir,
            @RequestParam(name = "sortField",defaultValue = "id") String sortField
    ){
        return ResponseEntity.ok(bookingServiceImp.getBookings(userId,packageId, pageNo,pageSize,sortDir,sortField));
    }
    @GetMapping("/get-total-sales")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> getTotalSales(){
        return ResponseEntity.ok(bookingServiceImp.getTotalPrice());
    }
    @GetMapping("/admin/get-by-id/{id}")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> getBookingById(@PathVariable("id") Long id){
        return ResponseEntity.ok(bookingServiceImp.getBookingById(id));
    }
    @PostMapping("/create")
    @RolesAllowed("user")
    public ResponseEntity<BaseResponse> createBooking(@Valid @RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingServiceImp.createBooking(bookingRequest));
    }
    @GetMapping("/get-by-id/{id}")
    @RolesAllowed("user")
    public ResponseEntity<BaseResponse> getBookingById(@RequestParam("userId") Long userId,@PathVariable("id") Long id){
        return ResponseEntity.ok(bookingServiceImp.getBookingById(userId,id));
    }
    @PutMapping("/update/{id}")
    @RolesAllowed("user")
    public ResponseEntity<BaseResponse> updateBooking(@Valid @RequestBody BookingRequest bookingRequest, @PathVariable("id") Long id){
        return ResponseEntity.ok(bookingServiceImp.updateBooking(id,bookingRequest));
    }
    @DeleteMapping("/delete/{id}")
    @RolesAllowed("user")
    public ResponseEntity<BaseResponse> deleteById(@RequestParam("userId") Long userId,@PathVariable("id") Long id){
        return ResponseEntity.ok(bookingServiceImp.deleteById(userId,id));
    }
}
