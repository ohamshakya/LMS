package com.project.lms.user.service.impl;

import com.project.lms.admin.entity.Book;
import com.project.lms.admin.repository.BookRepo;
import com.project.lms.user.entity.Rating;
import com.project.lms.user.repository.RatingRepo;
import com.project.lms.user.service.ContentBasedFilteringService;
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

    @Override
    public List<Book> recommend(Integer userId, int topN) {
        List<Rating> likedRatings = ratingRepo.findByUserIdAndRatingGreaterThanEqual(userId.longValue(), 4);
        Set<Integer> likedBookIds = likedRatings.stream()
                .map(r -> r.getBook().getId())
                .collect(Collectors.toSet());

        List<Book> likedBooks = likedRatings.stream()
                .map(Rating::getBook)
                .collect(Collectors.toList());

        List<Book> allBooks = bookRepo.findAll();

        Map<Book, Double> similarityScores = new HashMap<>();

        for (Book book : allBooks) {
            if (likedBookIds.contains(book.getId())) continue;

            double maxSimilarity = likedBooks.stream()
                    .mapToDouble(likedBook -> contentSimilarity(book, likedBook))
                    .max().orElse(0);

            if (maxSimilarity > 0) {
                similarityScores.put(book, maxSimilarity);
            }
        }

        return similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double contentSimilarity(Book book1, Book book2) {
        double score = 0;
        if (book1.getGenre().equalsIgnoreCase(book2.getGenre())) {
            score += 0.7;
        }
        if (book1.getAuthor().equalsIgnoreCase(book2.getAuthor())) {
            score += 0.3;
        }
        return score;
    }
}
