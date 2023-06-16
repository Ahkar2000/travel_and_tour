package com.example.tourism.controller;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.BookingRequest;
import com.example.tourism.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;

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
        return ResponseEntity.ok(bookingService.getBookings(userId,packageId, pageNo,pageSize,sortDir,sortField));
    }
    @GetMapping("/get-total-sales")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> getTotalSales(){
        return ResponseEntity.ok(bookingService.getTotalSales());
    }
    @GetMapping("/admin/get-by-id/{id}")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> getBookingById(@PathVariable("id") Long id){
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }
    @PostMapping("/create")
    @RolesAllowed("user")
    public ResponseEntity<BaseResponse> createBooking(@Valid @RequestBody BookingRequest bookingRequest, Principal principal){
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest, principal));
    }
    @GetMapping("/get-by-id/{id}")
    @RolesAllowed("user")
    public ResponseEntity<BaseResponse> getBookingById(@RequestParam("userId") Long userId,@PathVariable("id") Long id, Principal principal){
        return ResponseEntity.ok(bookingService.getBookingById(userId,id, principal));
    }
    @PutMapping("/update/{id}")
    @RolesAllowed("user")
    public ResponseEntity<BaseResponse> updateBooking(@Valid @RequestBody BookingRequest bookingRequest, @PathVariable("id") Long id, Principal principal){
        return ResponseEntity.ok(bookingService.updateBooking(id,bookingRequest, principal));
    }
    @DeleteMapping("/delete/{id}")
    @RolesAllowed("user")
    public ResponseEntity<BaseResponse> deleteById(@RequestParam("userId") Long userId,@PathVariable("id") Long id, Principal principal){
        return ResponseEntity.ok(bookingService.cancelBooking(userId,id, principal));
    }
}
