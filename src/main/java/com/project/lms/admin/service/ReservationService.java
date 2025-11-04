package com.project.lms.admin.service;

import com.project.lms.admin.dto.ReservationDto;
import com.project.lms.admin.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservationService {
    ReservationDto placeReservation(Integer bookId, Integer memberId);

    List<ReservationDto> getAllReservations();

    List<ReservationDto> getReservationsByMember(Integer memberId);

    void cancelReservation(Integer reservationId);

    void handleReservationOnBookReturn(Book book);

    ReservationDto getById(Integer id);

    Page<ReservationDto> getReservationWithPagination(Pageable pageable);
}
