package com.example.tourism.review.service;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.ReviewRequest;
import com.example.tourism.payLoad.request.UpdateReviewRequest;

import java.security.Principal;

public interface ReviewService {
    BaseResponse getAllReview();
    BaseResponse createReview(ReviewRequest reviewRequest, Principal principal);
    BaseResponse updateReview(Long id, UpdateReviewRequest updateReviewRequest, Principal principal);
    BaseResponse deleteReview(Long userId,Long id, Principal principal);
    BaseResponse findReviewById(Long id);
    BaseResponse findByUserId(Long userId);
    BaseResponse findByPackageId(Long packageId);
    BaseResponse getAverageRating();
}
