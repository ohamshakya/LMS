package com.project.lms.user.mapper;

import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Users;
import com.project.lms.user.dto.RatingDto;
import com.project.lms.user.entity.Rating;

public class RatingMapper {
    public static Rating toEntity(Users user, Book book, RatingDto ratingDto) {
        return Rating.builder()
                .user(user)
                .book(book)
                .rating(ratingDto.getRating())
                .review(ratingDto.getReview())
                .build();
    }

    public static RatingDto toDto(Rating rating){
        return RatingDto.builder()
                .rating(rating.getRating())
                .review(rating.getReview())
                .build();
    }
}
