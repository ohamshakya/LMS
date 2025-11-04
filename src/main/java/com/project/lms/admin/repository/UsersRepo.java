package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends JpaRepository<Users, Integer> {

    Users findByUsername(String username);

    @Query("""
        SELECT u FROM Users u
        WHERE (:query IS NULL 
               OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) 
               OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')))
    """)
    Page<Users> searchByFirstOrLastName(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(u) From Users u")
    Integer totalUsers();
}
