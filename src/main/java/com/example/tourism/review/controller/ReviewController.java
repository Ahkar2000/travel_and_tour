package com.example.tourism.review.controller;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.ReviewRequest;
import com.example.tourism.payLoad.request.UpdateReviewRequest;
import com.example.tourism.review.service.imp.ReviewServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    ReviewServiceImp reviewServiceImp;
    @GetMapping("")
    public ResponseEntity<BaseResponse> getAllReviews(){
        return ResponseEntity.ok(reviewServiceImp.getAllReview());
    }
    @GetMapping("/get-by-user-id/{userId}")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> findByUserId(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(reviewServiceImp.findByUserId(userId));
    }
    @GetMapping("/get-by-package-id/{packageId}")
    public ResponseEntity<BaseResponse> findByPackageId(@PathVariable("packageId") Long packageId){
        return ResponseEntity.ok(reviewServiceImp.findByPackageId(packageId));
    }
    @GetMapping("/{id}")
    @RolesAllowed("admin")
    public ResponseEntity<BaseResponse> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok(reviewServiceImp.findReviewById(id));
    }
    @GetMapping("/average-rating")
    public ResponseEntity<BaseResponse> getAverageRating(){
        return ResponseEntity.ok(reviewServiceImp.getAverageRating());
    }
    @PostMapping("/create")
    @RolesAllowed({"admin","user"})
    public ResponseEntity<BaseResponse> createReview(@Valid @RequestBody ReviewRequest reviewRequest, Principal principal){
        return ResponseEntity.ok(reviewServiceImp.createReview(reviewRequest,principal));
    }
    @PutMapping("/update/{id}")
    @RolesAllowed({"admin","user"})
    public ResponseEntity<BaseResponse> updateReview(@Valid @RequestBody UpdateReviewRequest updateReviewRequest, @PathVariable("id") Long id,Principal principal){
        return ResponseEntity.ok(reviewServiceImp.updateReview(id,updateReviewRequest,principal));
    }
    @DeleteMapping("/delete/{id}")
    @RolesAllowed({"admin","user"})
    public ResponseEntity<BaseResponse> deleteReview(@PathVariable("id") Long id,@RequestParam("userId") Long userId, Principal principal){
        return ResponseEntity.ok(reviewServiceImp.deleteReview(userId,id,principal));
    }

}
