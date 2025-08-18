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

    @Query("SELECT r.book.id FROM Rating r GROUP BY r.book.id ORDER BY AVG(r.rating) DESC")
    List<Integer> findTopRatedBookIds(Pageable pageable);
}
