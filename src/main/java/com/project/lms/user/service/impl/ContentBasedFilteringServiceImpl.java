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

    public ContentBasedFilteringServiceImpl(BookRepo bookRepo, RatingRepo ratingRepo) {
        this.bookRepo = bookRepo;
        this.ratingRepo = ratingRepo;
    }

    /*
    *
    * Recommends books similar to what you've liked
    * based on book content (genre, author).
    * */

    @Override
    public List<BookDto> recommend(Integer userId, int topN) {
        List<Rating> userRatings = ratingRepo.findByUserId(userId.longValue());

        if (userRatings.isEmpty()) {
            return getPopularBooks(topN);
        }
        UserProfile userProfile = buildUserProfile(userRatings);

        List<Book> allBooks = bookRepo.findAll();
        List<Book> unratedBooks = allBooks.stream()
                .filter(book -> userRatings.stream().noneMatch(r -> r.getBook().getId().equals(book.getId())))
                .toList();

        // Calculate similarity scores
        Map<Book, Double> similarityScores = unratedBooks.stream()
                .collect(Collectors.toMap(
                        book -> book,
                        book -> calculateContentSimilarity(book, userProfile)
                ));

        return similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .limit(topN)
                .map(entry -> BookMapper.toDto(entry.getKey()))
                .collect(Collectors.toList());
    }

    private UserProfile buildUserProfile(List<Rating> userRatings) {
        Map<String, Double> genreWeights = new HashMap<>();
        Map<String, Double> authorWeights = new HashMap<>();

        double totalWeight = 0;

        for (Rating rating : userRatings) {
            Book book = rating.getBook();
            double weight = rating.getRating(); // Higher ratings have more weight

            if (book.getGenre() != null) {
                genreWeights.merge(book.getGenre().toLowerCase(), weight, Double::sum);
            }
            if (book.getAuthor() != null) {
                authorWeights.merge(book.getAuthor().toLowerCase(), weight, Double::sum);
            }
            totalWeight += weight;
        }

        // Normalize weights
        if (totalWeight > 0) {
            double finalTotalWeight = totalWeight;
            genreWeights.replaceAll((k, v) -> v / finalTotalWeight);
            double finalTotalWeight1 = totalWeight;
            authorWeights.replaceAll((k, v) -> v / finalTotalWeight1);
        }

        return new UserProfile(genreWeights, authorWeights);
    }

    private double calculateContentSimilarity(Book book, UserProfile profile) {
        double similarity = 0.0;

        // Genre similarity
        if (book.getGenre() != null) {
            Double genreWeight = profile.genreWeights.get(book.getGenre().toLowerCase());
            if (genreWeight != null) {
                similarity += genreWeight * 0.6; // Genre is more important
            }
        }

        // Author similarity
        if (book.getAuthor() != null) {
            Double authorWeight = profile.authorWeights.get(book.getAuthor().toLowerCase());
            if (authorWeight != null) {
                similarity += authorWeight * 0.4; // Author is less important
            }
        }

        return similarity;
    }

    private List<BookDto> getPopularBooks(int topN) {
        // Implement popular books fallback
        return bookRepo.findTopRatedBooks(PageRequest.of(0, topN))
                .stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
    }

    private static class UserProfile {
        Map<String, Double> genreWeights;
        Map<String, Double> authorWeights;

        UserProfile(Map<String, Double> genreWeights, Map<String, Double> authorWeights) {
            this.genreWeights = genreWeights;
            this.authorWeights = authorWeights;
        }
    }
}
