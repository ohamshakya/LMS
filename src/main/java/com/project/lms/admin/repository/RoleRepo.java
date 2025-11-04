package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepo extends JpaRepository<Role,Integer> {

    @Query("""
    SELECT r FROM Role r
    WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', COALESCE(:keyword, ''), '%'))
""")
    Page<Role> searchRoles(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Role r")
    Integer totalRoles();
}
