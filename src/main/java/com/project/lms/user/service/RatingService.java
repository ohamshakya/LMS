package com.project.lms.user.service;

import com.project.lms.user.dto.RatingDto;
import com.project.lms.user.entity.Rating;

import java.util.List;

public interface RatingService {

    RatingDto create(Integer id,RatingDto ratingDto);

    RatingDto getById(Integer id);

    List<RatingDto> getAll();
}
