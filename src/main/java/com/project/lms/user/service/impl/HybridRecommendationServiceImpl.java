package com.project.lms.user.service.impl;

import com.project.lms.admin.dto.BookDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.mapper.BookMapper;
import com.project.lms.admin.repository.BookRepo;
import com.project.lms.user.repository.RatingRepo;
import com.project.lms.user.service.CollaborativeFilteringService;
import com.project.lms.user.service.ContentBasedFilteringService;
import com.project.lms.user.service.HybridRecommendationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HybridRecommendationServiceImpl implements HybridRecommendationService {
    private static final double COLLABORATIVE_WEIGHT = 0.6;
    private static final double CONTENT_BASED_WEIGHT = 0.4;

    private final ContentBasedFilteringService contentBasedService;
    private final CollaborativeFilteringService collaborativeFilteringService;
    private final BookRepo bookRepo;
    private final RatingRepo ratingRepo;

    public HybridRecommendationServiceImpl(ContentBasedFilteringService contentBasedService, CollaborativeFilteringService collaborativeFilteringService, BookRepo bookRepo, RatingRepo ratingRepo) {
        this.contentBasedService = contentBasedService;
        this.collaborativeFilteringService = collaborativeFilteringService;
        this.bookRepo = bookRepo;
        this.ratingRepo = ratingRepo;
    }

    @Override
    public List<BookDto> recommend(Integer userId, int topN) {
        // Get recommendations from both systems
        List<BookDto> contentBased = contentBasedService.recommend(userId, topN * 2); // Get more for ranking
        List<Integer> collaborativeIds = collaborativeFilteringService.recommend(userId, topN * 2);

        // Convert collaborative IDs to BookDto with scores
        Map<Integer, BookDto> collaborativeBooks = collaborativeIds.stream()
                .map(bookRepo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        Book::getId,
                        BookMapper::toDto,
                        (existing, replacement) -> existing
                ));

        // Combine and rank using weighted scores
        Map<Integer, Double> finalScores = new HashMap<>();

        // Add collaborative recommendations with weight
        int collaborativeRank = 1;
        for (Integer bookId : collaborativeIds) {
            double score = COLLABORATIVE_WEIGHT * (1.0 / collaborativeRank);
            finalScores.merge(bookId, score, Double::sum);
            collaborativeRank++;
        }

        // Add content-based recommendations with weight
        int contentRank = 1;
        for (BookDto bookDto : contentBased) {
            double score = CONTENT_BASED_WEIGHT * (1.0 / contentRank);
            finalScores.merge(bookDto.getId(), score, Double::sum);
            contentRank++;
        }

        // Get final ranked list
        List<BookDto> recommendedBooks = finalScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topN)
                .map(entry -> {
                    Integer bookId = entry.getKey();
                    BookDto bookDto = collaborativeBooks.get(bookId);
                    if (bookDto == null) {
                        // Fallback to content-based book
                        bookDto = contentBased.stream()
                                .filter(b -> b.getId().equals(bookId))
                                .findFirst()
                                .orElse(null);
                    }
                    return bookDto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Final fallback
        if (recommendedBooks.isEmpty()) {
            return getPopularBooks(topN);
        }

        return recommendedBooks;
    }

    private List<BookDto> getPopularBooks(int topN) {
        try {
            // Try to get top-rated books from ratings
            List<Integer> topRatedBookIds = ratingRepo.findTopRatedBookIds(PageRequest.of(0, topN));

            if (!topRatedBookIds.isEmpty()) {
                return topRatedBookIds.stream()
                        .map(bookRepo::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(BookMapper::toDto)
                        .collect(Collectors.toList());
            }

            // Fallback to most available books if no ratings
            return bookRepo.findAll(PageRequest.of(0, topN)).stream()
                    .map(BookMapper::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Final fallback - any books
            return bookRepo.findAll().stream()
                    .limit(topN)
                    .map(BookMapper::toDto)
                    .collect(Collectors.toList());
        }
    }
}
