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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HybridRecommendationServiceImpl implements HybridRecommendationService {
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
        List<BookDto> contentBased = contentBasedService.recommend(userId, topN);
        List<Integer> collaborativeIds = collaborativeFilteringService.recommend(userId, topN);

        LinkedHashSet<Integer> recommendedIds = new LinkedHashSet<>(collaborativeIds);
        contentBased.stream()
                .map(BookDto::getId)
                .forEach(recommendedIds::add);

        List<BookDto> recommendedBooks = recommendedIds.stream()
                .map(bookRepo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(BookMapper::toDto)
                .limit(topN)
                .collect(Collectors.toList());

        // Fallback logic
        if (recommendedBooks.isEmpty()) {
            System.out.println("No personalized recommendations found. Using top-rated books as fallback.");
            List<Integer> topRatedBookIds = ratingRepo.findTopRatedBookIds(PageRequest.of(0, topN));

            return topRatedBookIds.stream()
                    .map(bookRepo::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(BookMapper::toDto)
                    .collect(Collectors.toList());
        }

        return recommendedBooks;
    }
}
