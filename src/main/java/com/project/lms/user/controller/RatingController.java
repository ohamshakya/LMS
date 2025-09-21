package com.project.lms.user.controller;

import com.project.lms.common.util.ResponseWrapper;
import com.project.lms.user.dto.RatingDto;
import com.project.lms.user.service.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
@Slf4j
@Tag(name = "RATING", description = "RATING API FOR LMS")
@CrossOrigin("*")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/book/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseWrapper<RatingDto> create(@PathVariable Integer id, @RequestBody RatingDto ratingDto){
        RatingDto ratingDto1 = ratingService.create(id,ratingDto);
        return new ResponseWrapper<>(ratingDto1,"created successfully", HttpStatus.OK.value(),true);
    }
}
