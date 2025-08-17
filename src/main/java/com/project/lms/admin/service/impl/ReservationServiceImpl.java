package com.project.lms.admin.service.impl;

import com.project.lms.admin.dto.ReservationDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Membership;
import com.project.lms.admin.entity.Reservation;
import com.project.lms.admin.entity.Users;
import com.project.lms.admin.mapper.ReservationMapper;
import com.project.lms.admin.repository.BookRepo;
import com.project.lms.admin.repository.MembershipRepo;
import com.project.lms.admin.repository.ReservationRepo;
import com.project.lms.admin.service.EmailService;
import com.project.lms.admin.service.ReservationService;
import com.project.lms.common.enums.ReservationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepo reservationRepo;
    private final BookRepo bookRepo;
    private final MembershipRepo membershipRepo;
    private final EmailService emailService;

    public ReservationServiceImpl(ReservationRepo reservationRepo, BookRepo bookRepo, MembershipRepo membershipRepo, EmailService emailService) {
        this.reservationRepo = reservationRepo;
        this.bookRepo = bookRepo;
        this.membershipRepo = membershipRepo;
        this.emailService = emailService;
    }

    @Override
    public ReservationDto placeReservation(Integer bookId, Integer memberId) {
        log.info("inside place reservation : service");
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        Membership member = membershipRepo.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));

        Reservation reservation = Reservation.builder()
                .book(book)
                .membership(member)
                .reservationDate(LocalDate.now())
                .status(ReservationStatus.PENDING)
                .build();

        reservationRepo.save(reservation);
        return ReservationMapper.toDto(reservation);
    }

    @Override
    public List<ReservationDto> getAllReservations() {
        log.info("inside get all reservations repo");
        return reservationRepo.findAll().stream()
                .map(ReservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getReservationsByMember(Integer memberId) {
        log.info("inside get reservations by member : service" );
        return reservationRepo.findByMembershipId(memberId).stream()
                .map(ReservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelReservation(Integer reservationId) {
        log.info("inside delete by id : service");
        reservationRepo.deleteById(reservationId);
    }

    @Override
    public void handleReservationOnBookReturn(Book book) {
        List<Reservation> reservationQueue = reservationRepo
                .findByBookAndStatusOrderByReservationDate(book, ReservationStatus.PENDING);

        if (!reservationQueue.isEmpty()) {
            Reservation next = reservationQueue.get(0);

            next.setStatus(ReservationStatus.NOTIFIED);
            next.setNotificationDate(LocalDate.now());
            next.setExpiryDate(LocalDate.now().plusDays(3));

            book.setIsAvailable(false);  // Mark book as NOT available because it's on hold implicitly
            reservationRepo.save(next);
            bookRepo.save(book);

            sendHoldNotification(next.getMembership().getUsers(), book, next.getExpiryDate());
        } else {
            // No reservations, book is available
            book.setIsAvailable(true);
            bookRepo.save(book);
        }
    }

    private void sendHoldNotification(Users user, Book book, LocalDate expiryDate) {
        String to = user.getEmail();
        String subject = "Your Reserved Book is Now Available: " + book.getTitle();
        String body = String.format(
                "Hello %s,\n\n" +
                        "The book '%s' you reserved is now available for pickup.\n" +
                        "Please collect it by %s, after which the reservation will expire and the book will be offered to the next user.\n\n" +
                        "Thank you,\nLibrary Team",
                user.getFirstName(), book.getTitle(), expiryDate
        );

        emailService.sendSimpleEmail(to, subject, body);
    }
}
