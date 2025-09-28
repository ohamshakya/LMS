package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepo extends JpaRepository<Document,Integer> {
}
