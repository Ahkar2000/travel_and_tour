package com.example.tourism.review.service.imp;

import com.example.tourism.payLoad.response.BaseResponse;
import com.example.tourism.config.AppConfig;
import com.example.tourism.entity.Booking;
import com.example.tourism.payLoad.BaseBusiness;
import com.example.tourism.payLoad.request.ReviewRequest;
import com.example.tourism.payLoad.request.UpdateReviewRequest;
import com.example.tourism.payLoad.response.PackageRatingResponse;
import com.example.tourism.payLoad.response.ReviewResponse;
import com.example.tourism.repository.BookingRepository;
import com.example.tourism.repository.PackageRepository;
import com.example.tourism.repository.UserRepository;
import com.example.tourism.review.entity.Review;
import com.example.tourism.review.rowmapper.RatingMapper;
import com.example.tourism.review.rowmapper.ReviewMapper;
import com.example.tourism.review.service.ReviewService;
import com.example.tourism.service.KeyCloakService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ReviewServiceImp extends BaseBusiness implements ReviewService {
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    KeyCloakService keyCloakService;

    private final JdbcTemplate jdbcTemplate;
    private final AppConfig appConfig;
    private final ReviewMapper reviewMapper;
    private final RatingMapper ratingMapper;

    public ReviewServiceImp(AppConfig appConfig, ReviewMapper reviewMapper, RatingMapper ratingMapper) {
        this.reviewMapper = reviewMapper;
        this.appConfig = appConfig;
        this.ratingMapper = ratingMapper;
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(appConfig.getReviewDatasource().getSourceUrl());
        dataSource.setUsername(appConfig.getReviewDatasource().getUsername());
        dataSource.setPassword(appConfig.getReviewDatasource().getPassword());
        dataSource.setDriverClassName(appConfig.getReviewDatasource().getDriverClassName());
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public BaseResponse getAllReview() {
        try{
            String query = "SELECT * FROM review.review ORDER BY id ASC";
            List<Review> reviews = jdbcTemplate.query(query,reviewMapper);
            if(reviews.isEmpty()) return new BaseResponse("000","No results found.");
            return new BaseResponse("000",reviews.stream().map(this::convertReview));
        }catch (Exception e){
            return new BaseResponse("000",e.getMessage());
        }
    }

    @Override
    public BaseResponse createReview(ReviewRequest reviewRequest, Principal principal) {
        if (!String.valueOf(reviewRequest.getUserId()).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try{
            String query = "INSERT INTO review.review (user_id,package_id,review,rating,created_at) VALUES (?,?,?,?,?)";

            if(packageExists(reviewRequest.getPackageId()) == null) return new BaseResponse("404","Package does not exist.");

            List<Booking> bookingList= bookingRepository.isCustomer(reviewRequest.getUserId(), reviewRequest.getPackageId());
            if(bookingList.isEmpty()) return new BaseResponse("404","You have to book that package before giving a rating and review.");

            LocalDateTime reviewCreate = LocalDateTime.now();

            if(userExists(reviewRequest.getUserId()) == null) return new BaseResponse("404","User does not exist.");
            jdbcTemplate.update(query, reviewRequest.getUserId(),reviewRequest.getPackageId(),reviewRequest.getReview(),reviewRequest.getRating(), reviewCreate);

            return new BaseResponse("000",new ReviewResponse(reviewRequest.getUserId(),reviewRequest.getPackageId(),reviewRequest.getReview(),reviewRequest.getRating(),reviewCreate));
        }catch (Exception e){
            log.error("Error: "+e);
            return new BaseResponse("000",e.getMessage());
        }
    }

    @Override
    public BaseResponse updateReview(Long id, UpdateReviewRequest updateReviewRequest, Principal principal) {
        if (!String.valueOf(updateReviewRequest.getUserId()).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try{
            String query = "UPDATE review.review SET review = ? ,rating = ? WHERE id=?";
            ReviewResponse reviewResponse = findById(id);
            if(reviewResponse == null) return new BaseResponse("404","Review not found.");
            if(reviewResponse.getUserId() != updateReviewRequest.getUserId())  return new BaseResponse("409","You are not allowed.");
            jdbcTemplate.update(query,updateReviewRequest.getReview(),updateReviewRequest.getRating(),id);
            return new BaseResponse("000",new ReviewResponse(reviewResponse.getUserId(),reviewResponse.getPackageId(),updateReviewRequest.getReview(),updateReviewRequest.getRating(),reviewResponse.getCreatedAt()));
        }catch (Exception e){
            log.error("Error: "+e);
            return new BaseResponse("000",e.getMessage());
        }
    }

    @Override
    public BaseResponse deleteReview(Long userId,Long id, Principal principal) {
        if (!String.valueOf(userId).equals(keyCloakService.getKeycloakUserID(principal))) return new BaseResponse("403","You are not allowed.");
        try{
            String query = "DELETE FROM review.review WHERE id=?";
            ReviewResponse reviewResponse = findById(id);
            if(reviewResponse == null) return new BaseResponse("404","Review not found.");
            if(reviewResponse.getUserId() != userId)  return new BaseResponse("409","You are not allowed.");
            jdbcTemplate.update(query,new Object[]{id});
            return new BaseResponse("000","Review is deleted.");
        }catch (Exception e){
            return new BaseResponse("000",e.getMessage());
        }
    }

    @Override
    public BaseResponse findReviewById(Long id) {
        try{
            ReviewResponse reviewResponse = findById(id);
            if(reviewResponse == null) return new BaseResponse("404","Review not found.");
            return new BaseResponse("000",reviewResponse);
        }catch (Exception e){
            return new BaseResponse("000",e.getMessage());
        }
    }
    @Override
    public BaseResponse findByPackageId(Long packageId) {
        try{
            String query = "SELECT * FROM review.review WHERE package_id=? ORDER BY id ASC";
            List<Review> reviews = jdbcTemplate.query(query,new Object[]{packageId},reviewMapper);
            if(reviews.isEmpty()) return new BaseResponse("000","No results found.");
            return new BaseResponse("000",reviews.stream().map(this::convertReview));
        }catch (Exception e){
            return new BaseResponse("000",e.getMessage());
        }
    }

    @Override
    public BaseResponse getAverageRating() {
        try {
            String query = "SELECT p.package_name AS packageName, AVG(r.rating) AS averageRating " +
                    "FROM review.review AS r " +
                    "JOIN tourism.package AS p ON r.package_id = p.id " +
                    "GROUP BY p.package_name " +
                    "ORDER BY averageRating DESC";

            List<PackageRatingResponse> result = jdbcTemplate.query(query,ratingMapper);

            if (result.isEmpty()) {
                return new BaseResponse("404", "No packages found with ratings.");
            }

            return new BaseResponse("000", result);
        } catch (Exception e) {
            return new BaseResponse("000", e.getMessage());
        }
    }

    @Override
    public BaseResponse findByUserId(Long userId) {
        try{
            String query = "SELECT * FROM review.review WHERE user_id=? ORDER BY id ASC";
            List<Review> reviews = jdbcTemplate.query(query,new Object[]{userId},reviewMapper);
            if(reviews.isEmpty()) return new BaseResponse("000","No results found.");
            return new BaseResponse("000",reviews.stream().map(this::convertReview));
        }catch (Exception e){
            return new BaseResponse("000",e.getMessage());
        }
    }

    private ReviewResponse findById(Long id) {
        try {
            String query = "SELECT * FROM review.review WHERE id=?";
            Review review = jdbcTemplate.queryForObject(query, new Object[]{id}, reviewMapper);

            if (review != null) {
                return new ReviewResponse(review.getUserId(), review.getPackageId(), review.getReview(), review.getRating(), review.getCreatedAt());
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            return null;
        }
        return null;
    }
    private ReviewResponse convertReview(Review review){
        return new ReviewResponse(review.getUserId(),review.getPackageId(),review.getReview(),review.getRating(),review.getCreatedAt());
    }
}
