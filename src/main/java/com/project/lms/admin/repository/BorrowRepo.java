package com.project.lms.admin.repository;

import com.project.lms.admin.dto.BorrowResponse;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepo extends JpaRepository<Borrow, Integer> {

//    Page<Borrow> getAll(Pageable pageable);

    @Query(value = "SELECT * FROM borrow_details where user_id = :userId", nativeQuery = true)
    Page<Borrow> getAllById(@Param("userId")Integer userId,Pageable pageable);

    @Query("SELECT COUNT(b) FROM Borrow b")
    Integer totalBorrow();
}
