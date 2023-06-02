package com.example.tourism.repository;

import com.example.tourism.Queries.Queries;
import com.example.tourism.entity.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository {
    @Query(value = Queries.GET_USERS_ASC_WITH_PAGINATION,nativeQuery = true)
    List<User> getUsersAscWithPagination();
}
