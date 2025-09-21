package com.project.lms.user.controller;

import com.project.lms.admin.dto.BookDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.common.util.ResponseWrapper;
import com.project.lms.user.service.HybridRecommendationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendation")
@Tag(name = "RECOMMENDATION", description = "RECOMMENDATION API FOR LMS")
@CrossOrigin("*")
public class RecommendationAlgorithm {
    private final HybridRecommendationService hybridRecommendationService;

    public RecommendationAlgorithm(HybridRecommendationService hybridRecommendationService) {
        this.hybridRecommendationService = hybridRecommendationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseWrapper<List<BookDto>> recommendBooks(@PathVariable Integer userId,
                                                         @RequestParam(defaultValue = "10") int topN){
        List<BookDto> booksRecommend = hybridRecommendationService.recommend(userId,topN);
        return new ResponseWrapper<>(booksRecommend,"books retrieved with recommendation", HttpStatus.OK.value(),true);

    }
}

