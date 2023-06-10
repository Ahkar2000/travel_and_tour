package com.example.tourism.review.service;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.payLoad.request.ReviewRequest;
import com.example.tourism.payLoad.request.UpdateReviewRequest;

public interface ReviewService {
    BaseResponse getAllReview();
    BaseResponse createReview(ReviewRequest reviewRequest);
    BaseResponse updateReview(Long userId,Long id, UpdateReviewRequest updateReviewRequest);
    BaseResponse deleteReview(Long userId,Long id);
    BaseResponse findReviewById(Long id);
    BaseResponse findByUserId(Long userId);
    BaseResponse findByPackageId(Long packageId);
    BaseResponse getAverageRating();
}
