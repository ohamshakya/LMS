package com.project.lms.user.service.impl;

import com.project.lms.admin.dto.BookDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.mapper.BookMapper;
import com.project.lms.admin.repository.BookRepo;
import com.project.lms.user.repository.RatingRepo;
import com.project.lms.user.service.CollaborativeFilteringService;
import com.project.lms.user.service.ContentBasedFilteringService;
import com.project.lms.user.service.HybridRecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HybridRecommendationServiceImpl implements HybridRecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(HybridRecommendationServiceImpl.class);

    // Configurable weights
    private static final double COLLABORATIVE_WEIGHT = 0.6;
    private static final double CONTENT_BASED_WEIGHT = 0.4;
    private static final int EXPANDED_RECOMMENDATIONS_FACTOR = 3; // Get 3x more recommendations for ranking

    private final ContentBasedFilteringService contentBasedService;
    private final CollaborativeFilteringService collaborativeService;
    private final BookRepo bookRepo;
    private final RatingRepo ratingRepo;

    public HybridRecommendationServiceImpl(ContentBasedFilteringService contentBasedService,
                                           CollaborativeFilteringService collaborativeService,
                                           BookRepo bookRepo,
                                           RatingRepo ratingRepo) {
        this.contentBasedService = contentBasedService;
        this.collaborativeService = collaborativeService;
        this.bookRepo = bookRepo;
        this.ratingRepo = ratingRepo;
    }

    @Override
    public List<BookDto> recommend(Integer userId, int topN) {
        try {
            logger.info("Generating hybrid recommendations for user: {}, topN: {}", userId, topN);

            // Get expanded recommendations from both systems
            int expandedTopN = topN * EXPANDED_RECOMMENDATIONS_FACTOR;

            List<BookDto> contentBasedRecs = getContentBasedRecommendations(userId, expandedTopN);
            List<Integer> collaborativeRecs = getCollaborativeRecommendations(userId, expandedTopN);

            logger.debug("Content-based recommendations: {}", contentBasedRecs.size());
            logger.debug("Collaborative recommendations: {}", collaborativeRecs.size());

            // If both systems return empty, use fallback
            if (contentBasedRecs.isEmpty() && collaborativeRecs.isEmpty()) {
                logger.info("Both recommendation systems returned empty, using popular books fallback");
                return getPopularBooks(topN);
            }

            // Create book lookup map for efficient access
            Map<Integer, BookDto> bookLookup = createBookLookup(contentBasedRecs, collaborativeRecs);

            // Calculate hybrid scores
            Map<Integer, Double> hybridScores = calculateHybridScores(
                    contentBasedRecs, collaborativeRecs, bookLookup.keySet());

            // Get final ranked recommendations
            List<BookDto> finalRecommendations = getRankedRecommendations(hybridScores, bookLookup, topN);

            logger.info("Generated {} hybrid recommendations for user: {}", finalRecommendations.size(), userId);
            return finalRecommendations;

        } catch (Exception e) {
            logger.error("Error generating hybrid recommendations for user: {}", userId, e);
            return getPopularBooks(topN);
        }
    }

    private List<BookDto> getContentBasedRecommendations(Integer userId, int topN) {
        try {
            return contentBasedService.recommend(userId, topN);
        } catch (Exception e) {
            logger.warn("Content-based recommendation failed for user: {}", userId, e);
            return Collections.emptyList();
        }
    }

    private List<Integer> getCollaborativeRecommendations(Integer userId, int topN) {
        try {
            return collaborativeService.recommend(userId, topN);
        } catch (Exception e) {
            logger.warn("Collaborative recommendation failed for user: {}", userId, e);
            return Collections.emptyList();
        }
    }

    private Map<Integer, BookDto> createBookLookup(List<BookDto> contentBasedRecs, List<Integer> collaborativeRecs) {
        Map<Integer, BookDto> lookup = new HashMap<>();

        // Add content-based recommendations to lookup
        contentBasedRecs.forEach(book -> lookup.put(book.getId(), book));

        // Fetch and add collaborative recommendations to lookup
        if (!collaborativeRecs.isEmpty()) {
            List<Integer> missingBookIds = collaborativeRecs.stream()
                    .filter(bookId -> !lookup.containsKey(bookId))
                    .collect(Collectors.toList());

            if (!missingBookIds.isEmpty()) {
                Map<Integer, BookDto> collaborativeBooks = bookRepo.findAllById(missingBookIds)
                        .stream()
                        .map(BookMapper::toDto)
                        .collect(Collectors.toMap(BookDto::getId, Function.identity()));
                lookup.putAll(collaborativeBooks);
            }
        }

        return lookup;
    }

    private Map<Integer, Double> calculateHybridScores(List<BookDto> contentBasedRecs,
                                                       List<Integer> collaborativeRecs,
                                                       Set<Integer> availableBookIds) {
        Map<Integer, Double> hybridScores = new HashMap<>();

        // Calculate collaborative scores with rank-based weighting
        calculateRankBasedScores(collaborativeRecs, hybridScores, COLLABORATIVE_WEIGHT, availableBookIds);

        // Calculate content-based scores with rank-based weighting
        List<Integer> contentBasedIds = contentBasedRecs.stream()
                .map(BookDto::getId)
                .collect(Collectors.toList());
        calculateRankBasedScores(contentBasedIds, hybridScores, CONTENT_BASED_WEIGHT, availableBookIds);

        return hybridScores;
    }

    private void calculateRankBasedScores(List<Integer> recommendations,
                                          Map<Integer, Double> hybridScores,
                                          double weight,
                                          Set<Integer> availableBookIds) {
        if (recommendations.isEmpty()) return;

        double maxScore = weight;
        double decayFactor = maxScore / recommendations.size();

        for (int rank = 0; rank < recommendations.size(); rank++) {
            Integer bookId = recommendations.get(rank);

            // Only consider books that are available in our lookup
            if (!availableBookIds.contains(bookId)) continue;

            // Higher rank = higher score, with linear decay
            double rankScore = maxScore - (decayFactor * rank);
            if (rankScore < 0) rankScore = 0;

            hybridScores.merge(bookId, rankScore, Double::sum);
        }
    }

    private List<BookDto> getRankedRecommendations(Map<Integer, Double> hybridScores,
                                                   Map<Integer, BookDto> bookLookup,
                                                   int topN) {
        return hybridScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topN)
                .map(entry -> bookLookup.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<BookDto> getPopularBooks(int topN) {
        try {
            logger.debug("Using popular books fallback for topN: {}", topN);

            // Try to get top-rated books
            List<BookDto> topRated = getTopRatedBooks(topN);
            if (!topRated.isEmpty()) {
                return topRated;
            }

            // Fallback to recently added books
            List<BookDto> recentBooks = getRecentBooks(topN);
            if (!recentBooks.isEmpty()) {
                return recentBooks;
            }

            // Final fallback - any books
            return bookRepo.findAll(PageRequest.of(0, topN))
                    .stream()
                    .map(BookMapper::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error in popular books fallback", e);
            // Ultimate fallback
            return bookRepo.findAll().stream()
                    .limit(topN)
                    .map(BookMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    private List<BookDto> getTopRatedBooks(int topN) {
        try {
            // Assuming you have a method to find top rated books
            return bookRepo.findTopRatedBooks(PageRequest.of(0, topN))
                    .stream()
                    .map(BookMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.debug("Top rated books query failed, using alternative method");
            return Collections.emptyList();
        }
    }

    private List<BookDto> getRecentBooks(int topN) {
        try {
            return bookRepo.findByOrderByCreatedAtDesc(PageRequest.of(0, topN))
                    .stream()
                    .map(BookMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.debug("Recent books query failed");
            return Collections.emptyList();
        }
    }
}
