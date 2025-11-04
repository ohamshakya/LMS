package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Users;
import com.project.lms.common.enums.PaymentStatus;
import com.project.lms.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByBorrowId(Integer borrowId);

    Optional<Payment> findByTransactionId(String transactionId);

    Page<Payment> findAllByCreatedAtAfter(LocalDateTime date, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.borrow.user = :user AND p.createdAt >= :date")
    Page<Payment> findByBorrowUserAndCreatedAtAfter(@Param("user") Users user,
                                                    @Param("date") LocalDateTime date,
                                                    Pageable pageable);

    Page<Payment> findByBorrowUser(Users user, Pageable pageable);

    Optional<Payment> findByBorrowIdAndStatus(Integer borrowId, PaymentStatus status);
}