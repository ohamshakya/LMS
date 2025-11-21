package com.project.lms.user.repository;

import com.project.lms.user.entity.Rating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepo extends JpaRepository<Rating,Integer> {
    List<Rating> findByUserId(Long userId);

    List<Rating> findByUserIdAndRatingGreaterThanEqual(Long userId, int rating);

    @Query("SELECT DISTINCT r.user.id FROM Rating r")
    List<Long> findDistinctUserIds();

    @Query("""
    SELECT b FROM Book b
    JOIN (
        SELECT r.book.id AS bookId, AVG(r.rating) AS avgRating
        FROM Rating r
        GROUP BY r.book.id
    ) agg ON agg.bookId = b.id
    ORDER BY agg.avgRating DESC
    """)
    List<Integer> findTopRatedBooks(Pageable pageable);
}
