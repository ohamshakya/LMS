package com.project.lms.user.service.impl;

import com.project.lms.user.entity.Rating;
import com.project.lms.user.repository.RatingRepo;
import com.project.lms.user.service.CollaborativeFilteringService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollaborativeFilteringServiceImpl implements CollaborativeFilteringService {
    private final RatingRepo ratingRepo;
    private final int MIN_COMMON_BOOKS = 2; // Minimum books both users must have rated
    private final int TOP_SIMILAR_USERS = 10; // Configurable

    public CollaborativeFilteringServiceImpl(RatingRepo ratingRepo) {
        this.ratingRepo = ratingRepo;
    }
    /*
    *
    * Collaborative filtering:- find users with similar taste and recommend books that you haven't read
    * */
    @Override
    public List<Integer> recommend(Integer userId, int topN) {
        List<Rating> currentUserRatings = ratingRepo.findByUserId(userId.longValue());
        if (currentUserRatings.isEmpty()) {
            return Collections.emptyList(); // No basis for recommendations
        }

        Map<Integer, Integer> currentRatings = currentUserRatings.stream()
                .collect(Collectors.toMap(r -> r.getBook().getId(), Rating::getRating));

        List<Long> allUserIds = ratingRepo.findDistinctUserIds();
        allUserIds.remove(userId.longValue());

        if (allUserIds.isEmpty()) {
            return Collections.emptyList(); // No other users to compare with
        }

        // Find similar users with proper filtering
        List<SimilarUser> similarUsers = allUserIds.stream()
                .map(otherUserId -> {
                    List<Rating> otherRatings = ratingRepo.findByUserId(otherUserId);
                    Map<Integer, Integer> otherRatingsMap = otherRatings.stream()
                            .collect(Collectors.toMap(r -> r.getBook().getId(), Rating::getRating));

                    double similarity = calculateSimilarity(currentRatings, otherRatingsMap);
                    return new SimilarUser(otherUserId, similarity, otherRatingsMap);
                })
                .filter(su -> su.similarity > 0.1) // Minimum similarity threshold
                .filter(su -> su.ratings.size() >= MIN_COMMON_BOOKS) // Enough data
                .sorted(Comparator.comparing(SimilarUser::getSimilarity).reversed())
                .limit(TOP_SIMILAR_USERS)
                .collect(Collectors.toList());

        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }

        // Calculate weighted scores for books
        Map<Integer, Double> bookScores = new HashMap<>();
        Map<Integer, Double> similaritySums = new HashMap<>();

        for (SimilarUser similarUser : similarUsers) {
            for (Map.Entry<Integer, Integer> entry : similarUser.ratings.entrySet()) {
                Integer bookId = entry.getKey();
                Integer rating = entry.getValue();

                if (!currentRatings.containsKey(bookId)) { // Only recommend unrated books
                    double weightedScore = rating * similarUser.similarity;

                    bookScores.merge(bookId, weightedScore, Double::sum);
                    similaritySums.merge(bookId, similarUser.similarity, Double::sum);
                }
            }
        }

        // Calculate final scores and return top N
        return bookScores.entrySet().stream()
                .map(entry -> {
                    Integer bookId = entry.getKey();
                    double totalSimilarity = similaritySums.get(bookId);
                    double finalScore = entry.getValue() / totalSimilarity;
                    return new BookScore(bookId, finalScore);
                })
                .sorted(Comparator.comparing(BookScore::getScore).reversed())
                .map(BookScore::getBookId)
                .limit(topN)
                .collect(Collectors.toList());
    }

    private double calculateSimilarity(Map<Integer, Integer> user1, Map<Integer, Integer> user2) {
        // Jaccard similarity for liked items (your current approach)
        Set<Integer> liked1 = user1.entrySet().stream()
                .filter(e -> e.getValue() >= 4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Integer> liked2 = user2.entrySet().stream()
                .filter(e -> e.getValue() >= 4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Integer> intersection = new HashSet<>(liked1);
        intersection.retainAll(liked2);
        Set<Integer> union = new HashSet<>(liked1);
        union.addAll(liked2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    // Helper classes
    private static class SimilarUser {
        Long userId;
        double similarity;
        Map<Integer, Integer> ratings;

        SimilarUser(Long userId, double similarity, Map<Integer, Integer> ratings) {
            this.userId = userId;
            this.similarity = similarity;
            this.ratings = ratings;
        }

        double getSimilarity() { return similarity; }
    }

    private static class BookScore {
        Integer bookId;
        double score;

        BookScore(Integer bookId, double score) {
            this.bookId = bookId;
            this.score = score;
        }

        double getScore() { return score; }
        Integer getBookId() { return bookId; }
    }

}
