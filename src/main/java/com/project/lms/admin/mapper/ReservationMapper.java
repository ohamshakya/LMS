package com.project.lms.admin.mapper;

import com.project.lms.admin.dto.ReservationDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Membership;
import com.project.lms.admin.entity.Reservation;
import com.project.lms.common.enums.ReservationStatus;

import java.time.LocalDate;

public class ReservationMapper {
    public static ReservationDto toDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setBookId(reservation.getBook().getId());
        dto.setBookTitle(reservation.getBook().getTitle());
        dto.setMemberId(reservation.getMembership().getId());
        dto.setMemberName(reservation.getMembership().getUsers().getFirstName().concat(reservation.getMembership().getUsers().getLastName()));
        dto.setPhoneNumber(reservation.getMembership().getUsers().getPhoneNumber());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setNotificationDate(reservation.getNotificationDate());
        dto.setExpiryDate(reservation.getExpiryDate());
        dto.setStatus(reservation.getStatus());
        return dto;
    }

    public static Reservation toEntity(ReservationDto dto, Book book, Membership member) {
        return Reservation.builder()
                .book(book)
                .membership(member)
                .reservationDate(LocalDate.now())
                .status(ReservationStatus.PENDING)
                .build();
    }
}
