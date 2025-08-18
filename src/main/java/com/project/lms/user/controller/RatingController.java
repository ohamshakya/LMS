package com.project.lms.user.controller;

import com.project.lms.common.util.ResponseWrapper;
import com.project.lms.user.dto.RatingDto;
import com.project.lms.user.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
@Slf4j
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/book/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseWrapper<RatingDto> create(@PathVariable Integer id, @RequestBody RatingDto ratingDto){
        RatingDto ratingDto1 = ratingService.create(id,ratingDto);
        return new ResponseWrapper<>(ratingDto1,"created successfully", HttpStatus.OK.value());
    }
}
