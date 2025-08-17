package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Reservation;
import com.project.lms.common.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation,Integer> {
    List<Reservation> findByMembershipId(Integer memberId);
    List<Reservation> findByBookId(Integer bookId);
    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByBookAndStatusOrderByReservationDate(Book book, ReservationStatus status);
}
