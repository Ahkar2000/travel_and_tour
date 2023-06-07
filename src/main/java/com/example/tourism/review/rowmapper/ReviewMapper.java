package com.example.tourism.review.rowmapper;

import com.example.tourism.review.entity.Review;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class ReviewMapper implements RowMapper<Review> {

    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setId(rs.getLong("id"));
        review.setReview(rs.getString("review"));
        review.setUserId(rs.getLong("user_id"));
        review.setPackageId(rs.getLong("package_id"));
        review.setRating(rs.getDouble("rating"));
        review.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return review;
    }
}
