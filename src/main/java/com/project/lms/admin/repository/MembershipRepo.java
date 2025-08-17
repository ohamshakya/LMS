package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepo extends JpaRepository<Membership,Integer> {

    @Query("SELECT COUNT(m) FROM Membership m")
    Integer totalMember();

    Membership findByUsersId(Integer userId);
}
