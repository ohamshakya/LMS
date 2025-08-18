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

    public CollaborativeFilteringServiceImpl(RatingRepo ratingRepo) {
        this.ratingRepo = ratingRepo;
    }

    @Override
    public List<Integer> recommend(Integer userId, int topN) {
        List<Long> allUserIds = ratingRepo.findDistinctUserIds();
        allUserIds.remove(userId.longValue());

        Map<Long, Map<Integer, Integer>> userRatingsMap = new HashMap<>();

        for (Long uid : allUserIds) {
            List<Rating> ratings = ratingRepo.findByUserId(uid);
            Map<Integer, Integer> bookRatings = ratings.stream()
                    .collect(Collectors.toMap(
                            r -> r.getBook().getId(),
                            Rating::getRating,
                            (existing, replacement) -> replacement // or Math.max(existing, replacement)
                    ));
            userRatingsMap.put(uid, bookRatings);
        }

        List<Rating> currentUserRatings = ratingRepo.findByUserId(userId.longValue());
        Map<Integer, Integer> currentRatings = currentUserRatings.stream()
                .collect(Collectors.toMap(
                        r -> r.getBook().getId(),
                        Rating::getRating,
                        (existing, replacement) -> replacement // same merge strategy here
                ));

        Map<Long, Double> similarityScores = new HashMap<>();

        for (Long otherUserId : allUserIds) {
            Map<Integer, Integer> otherRatings = userRatingsMap.get(otherUserId);
            double similarity = calculateSimilarity(currentRatings, otherRatings);
            similarityScores.put(otherUserId, similarity);
        }

        List<Long> topUsers = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Set<Integer> recommendedBookIds = new LinkedHashSet<>();

        for (Long similarUserId : topUsers) {
            Map<Integer, Integer> ratings = userRatingsMap.get(similarUserId);
            for (Map.Entry<Integer, Integer> entry : ratings.entrySet()) {
                Integer bookId = entry.getKey();
                Integer rating = entry.getValue();
                if (rating >= 4 && !currentRatings.containsKey(bookId)) {
                    recommendedBookIds.add(bookId);
                }
            }
        }

        return recommendedBookIds.stream().limit(topN).collect(Collectors.toList());
    }

    private double calculateSimilarity(Map<Integer, Integer> ratings1, Map<Integer, Integer> ratings2) {
        Set<Integer> liked1 = ratings1.entrySet().stream()
                .filter(e -> e.getValue() >= 4).map(Map.Entry::getKey).collect(Collectors.toSet());
        Set<Integer> liked2 = ratings2.entrySet().stream()
                .filter(e -> e.getValue() >= 4).map(Map.Entry::getKey).collect(Collectors.toSet());

        Set<Integer> intersection = new HashSet<>(liked1);
        intersection.retainAll(liked2);
        Set<Integer> union = new HashSet<>(liked1);
        union.addAll(liked2);

        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }

}
