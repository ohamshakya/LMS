package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Borrow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepo extends JpaRepository<Borrow, Integer> {

//    Page<Borrow> getAll(Pageable pageable);


}
