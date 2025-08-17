package com.project.lms.user.service.impl;

import com.project.lms.common.exception.ResourceNotFoundException;
import com.project.lms.common.util.Messages;
import com.project.lms.user.dto.RatingDto;
import com.project.lms.user.entity.Rating;
import com.project.lms.user.mapper.RatingMapper;
import com.project.lms.user.repository.RatingRepo;
import com.project.lms.user.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepo ratingRepo;

    public RatingServiceImpl(RatingRepo ratingRepo) {
        this.ratingRepo = ratingRepo;
    }

    @Override
    public RatingDto create(RatingDto ratingDto) {
        return null;
    }

    @Override
    public RatingDto getById(Integer id) {
        Rating notFound = ratingRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.RATING_NOT_FOUND));
        return RatingMapper.toDto(notFound);
    }

    @Override
    public List<RatingDto> getAll() {
        return ratingRepo.findAll().stream().map(RatingMapper::toDto).collect(Collectors.toList());
    }
}
