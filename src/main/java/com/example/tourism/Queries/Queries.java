package com.example.tourism.Queries;

public class Queries {
    public static final String GET_USERS_ASC_WITH_PAGINATION = "SELECT * FROM user ORDER BY id ASC LIMIT ?1 OFFSET ?2 ";
    public static final String GET_USERS_COUNT_WITH_PAGINATION = "SELECT COUNT(*) FROM user";
}
