package com.example.tourism.review.rowmapper;

import com.example.tourism.payLoad.response.PackageRatingResponse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingMapper implements RowMapper<PackageRatingResponse> {
    @Override
    public PackageRatingResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        PackageRatingResponse packageRatingResponse = new PackageRatingResponse();

        double number = rs.getDouble("averageRating");
        String formattedNumber = String.format("%.1f", number);
        double formattedValue = Double.parseDouble(formattedNumber);

        packageRatingResponse.setPackageName(rs.getString("packageName"));
        packageRatingResponse.setAverageRating(formattedValue);
        return packageRatingResponse;
    }
}
