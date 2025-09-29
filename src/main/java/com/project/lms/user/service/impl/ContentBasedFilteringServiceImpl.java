package com.project.lms.user.service.impl;

import com.project.lms.admin.dto.BookDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.mapper.BookMapper;
import com.project.lms.admin.repository.BookRepo;
import com.project.lms.user.entity.Rating;
import com.project.lms.user.repository.RatingRepo;
import com.project.lms.user.service.ContentBasedFilteringService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentBasedFilteringServiceImpl implements ContentBasedFilteringService {
    private final BookRepo bookRepo;
    private final RatingRepo ratingRepo;

    // Configurable weights for similarity calculation
    private static final double GENRE_WEIGHT = 0.6;
    private static final double AUTHOR_WEIGHT = 0.4;
    private static final double MIN_SIMILARITY_THRESHOLD = 0.1;

    public ContentBasedFilteringServiceImpl(BookRepo bookRepo, RatingRepo ratingRepo) {
        this.bookRepo = bookRepo;
        this.ratingRepo = ratingRepo;
    }

    @Override
    public List<BookDto> recommend(Integer userId, int topN) {
        List<Rating> userRatings = ratingRepo.findByUserId(userId.longValue());

        if (userRatings.isEmpty()) {
            return getPopularBooks(topN);
        }

        UserProfile userProfile = buildUserProfile(userRatings);

        // Early return if profile is empty
        if (userProfile.isEmpty()) {
            return getPopularBooks(topN);
        }

        List<Book> allBooks = bookRepo.findAll();
        Set<Integer> ratedBookIds = userRatings.stream()
                .map(rating -> rating.getBook().getId())
                .collect(Collectors.toSet());

        // Calculate similarity scores with filtering
        List<BookScore> recommendations = allBooks.stream()
                .filter(book -> !ratedBookIds.contains(book.getId()))
                .map(book -> new BookScore(book, calculateContentSimilarity(book, userProfile)))
                .filter(bookScore -> bookScore.score > MIN_SIMILARITY_THRESHOLD)
                .sorted(Comparator.comparing(BookScore::getScore).reversed())
                .limit(topN)
                .collect(Collectors.toList());

        // Fallback to popular books if no good recommendations found
        if (recommendations.isEmpty()) {
            return getPopularBooks(topN);
        }

        return recommendations.stream()
                .map(bookScore -> BookMapper.toDto(bookScore.book))
                .collect(Collectors.toList());
    }

    private UserProfile buildUserProfile(List<Rating> userRatings) {
        Map<String, Double> genreWeights = new HashMap<>();
        Map<String, Double> authorWeights = new HashMap<>();

        double totalPositiveWeight = 0;

        // Only consider positive ratings (>= 3) for building profile
        for (Rating rating : userRatings) {
            if (rating.getRating() < 3) continue; // Skip low ratings

            Book book = rating.getBook();
            double weight = rating.getRating(); // Higher ratings have more weight

            // Handle genre (consider multiple genres if separated by comma)
            if (book.getGenre() != null && !book.getGenre().trim().isEmpty()) {
                String[] genres = book.getGenre().toLowerCase().split("\\s*,\\s*");
                for (String genre : genres) {
                    genreWeights.merge(genre.trim(), weight, Double::sum);
                }
            }

            // Handle author
            if (book.getAuthor() != null && !book.getAuthor().trim().isEmpty()) {
                authorWeights.merge(book.getAuthor().toLowerCase().trim(), weight, Double::sum);
            }

            totalPositiveWeight += weight;
        }

        // Normalize weights only if we have positive ratings
        if (totalPositiveWeight > 0) {
            normalizeWeights(genreWeights, totalPositiveWeight);
            normalizeWeights(authorWeights, totalPositiveWeight);
        }

        return new UserProfile(genreWeights, authorWeights);
    }

    private void normalizeWeights(Map<String, Double> weights, double totalWeight) {
        weights.replaceAll((k, v) -> v / totalWeight);
    }

    private double calculateContentSimilarity(Book book, UserProfile profile) {
        double similarity = 0.0;
        int featureCount = 0;

        // Genre similarity (handle multiple genres)
        if (book.getGenre() != null && !book.getGenre().trim().isEmpty()) {
            String[] bookGenres = book.getGenre().toLowerCase().split("\\s*,\\s*");
            double maxGenreSimilarity = 0.0;

            for (String genre : bookGenres) {
                String trimmedGenre = genre.trim();
                Double genreWeight = profile.genreWeights.get(trimmedGenre);
                if (genreWeight != null && genreWeight > maxGenreSimilarity) {
                    maxGenreSimilarity = genreWeight;
                }
            }
            similarity += maxGenreSimilarity * GENRE_WEIGHT;
            if (maxGenreSimilarity > 0) featureCount++;
        }

        // Author similarity
        if (book.getAuthor() != null && !book.getAuthor().trim().isEmpty()) {
            String authorKey = book.getAuthor().toLowerCase().trim();
            Double authorWeight = profile.authorWeights.get(authorKey);
            if (authorWeight != null) {
                similarity += authorWeight * AUTHOR_WEIGHT;
                featureCount++;
            }
        }

        // If no features match, return 0
        return featureCount > 0 ? similarity : 0.0;
    }

    private List<BookDto> getPopularBooks(int topN) {
        return bookRepo.findTopRatedBooks(PageRequest.of(0, topN))
                .stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
    }

    // Helper classes
    private static class UserProfile {
        Map<String, Double> genreWeights;
        Map<String, Double> authorWeights;

        UserProfile(Map<String, Double> genreWeights, Map<String, Double> authorWeights) {
            this.genreWeights = genreWeights != null ? genreWeights : new HashMap<>();
            this.authorWeights = authorWeights != null ? authorWeights : new HashMap<>();
        }

        boolean isEmpty() {
            return genreWeights.isEmpty() && authorWeights.isEmpty();
        }
    }

    private static class BookScore {
        Book book;
        double score;

        BookScore(Book book, double score) {
            this.book = book;
            this.score = score;
        }

        double getScore() { return score; }
    }
}
