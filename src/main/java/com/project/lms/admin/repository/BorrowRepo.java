package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRepo extends JpaRepository<Borrow, Integer> {

//    Page<Borrow> getAll(Pageable pageable);



}
