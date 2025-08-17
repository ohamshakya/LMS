package com.project.lms.user.service.impl;

import com.project.lms.admin.entity.Book;
import com.project.lms.admin.repository.BookRepo;
import com.project.lms.user.service.CollaborativeFilteringService;
import com.project.lms.user.service.ContentBasedFilteringService;
import com.project.lms.user.service.HybridRecommendationService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HybridRecommendationServiceImpl implements HybridRecommendationService {
    private final ContentBasedFilteringService contentBasedService;
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final BookRepo bookRepo;

    public HybridRecommendationServiceImpl(ContentBasedFilteringService contentBasedService, CollaborativeFilteringService collaborativeFilteringService, BookRepo bookRepo) {
        this.contentBasedService = contentBasedService;
        this.collaborativeFilteringService = collaborativeFilteringService;
        this.bookRepo = bookRepo;
    }


    @Override
    public List<Book> recommend(Integer userId, int topN) {
        List<Book> contentBased = contentBasedService.recommend(userId, topN);
        List<Integer> collaborativeIds = collaborativeFilteringService.recommend(userId, topN);

        LinkedHashSet<Integer> recommendedIds = new LinkedHashSet<>(collaborativeIds);
        contentBased.stream()
                .map(Book::getId)
                .forEach(recommendedIds::add);

        return recommendedIds.stream()
                .map(bookRepo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .limit(topN)
                .collect(Collectors.toList());
    }
}
