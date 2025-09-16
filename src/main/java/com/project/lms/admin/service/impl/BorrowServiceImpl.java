package com.project.lms.admin.service.impl;

import com.project.lms.admin.dto.BorrowDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Borrow;
import com.project.lms.admin.mapper.BorrowMapper;
import com.project.lms.admin.repository.BookRepo;
import com.project.lms.admin.repository.BorrowRepo;
import com.project.lms.admin.service.BorrowService;
import com.project.lms.admin.service.ReservationService;
import com.project.lms.common.exception.BorrowException;
import com.project.lms.common.exception.ResourceNotFoundException;
import com.project.lms.common.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class BorrowServiceImpl implements BorrowService {
    private final BorrowRepo borrowRepo;
    private final BookRepo bookRepo;
    private final ReservationService reservationService;

    public BorrowServiceImpl(BorrowRepo borrowRepo, BookRepo bookRepo, ReservationService reservationService) {
        this.borrowRepo = borrowRepo;
        this.bookRepo = bookRepo;
        this.reservationService = reservationService;
    }

    @Override
    public BorrowDto create(Integer id, BorrowDto borrowDto) {
        log.info("inside create borrow : service");

        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Messages.BORROW_NOT_FOUND));

        // Check if any copies are available
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new BorrowException(Messages.BOOK_ALREADY_BORROWED);
        }

        // Decrease available copies by 1
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        // Update isAvailable based on availableCopies
        book.setIsAvailable(book.getAvailableCopies() > 0);

        // Save the updated book info
        bookRepo.save(book);

        // Create borrow entity and save
        Borrow borrow = BorrowMapper.toEntity(book, borrowDto);
        borrowRepo.save(borrow);

        return BorrowMapper.toDto(borrow);
    }

    @Override
    public BorrowDto update(Integer id, BorrowDto borrowDto) {
        log.info("inside borrow update : service");
        Borrow existingBorrow = borrowRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.BORROW_NOT_FOUND));
        existingBorrow.setBorrowDate(borrowDto.getBorrowDate());
        existingBorrow.setReturnDate(borrowDto.getReturnDate());
        borrowRepo.save(existingBorrow);
        return BorrowMapper.toDto(existingBorrow);
    }

    @Override
    public BorrowDto getById(Integer id) {
        log.info("inside get borrow by id : service");
        Borrow existedBorrow = borrowRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.BORROW_NOT_FOUND));
        return BorrowMapper.toDto(existedBorrow);
    }

    @Override
    public String returnedBook(Integer id) {
        log.info("inside return book : service");
        try {
            Borrow exists = checkIfExists(id);

            if (exists.getIsReturned()) {
                log.error("Sorry, cannot return the book for ID: {}", id);
                throw new BorrowException("Sorry, book with ID " + id + " is already returned.");
            }
            LocalDate returnedDate = LocalDate.now();
            exists.setReturnDate(returnedDate);
            exists.setIsReturned(true);

            Book book = exists.getBook();
            book.setIsAvailable(true);
            long overdueDays = returnedDate.toEpochDay() - exists.getDueDate().toEpochDay();

            if (overdueDays > 0) {
                BigDecimal finePerDay = BigDecimal.valueOf(1.0);
                BigDecimal fineAmount = BigDecimal.valueOf(overdueDays).multiply(finePerDay);
                exists.setFineAmount(fineAmount);
            } else {
                exists.setFineAmount(BigDecimal.ZERO);
            }
            reservationService.handleReservationOnBookReturn(book);

            borrowRepo.save(exists);

            return "Book returned successfully for ID: " + id;

        } catch (Exception e) {
            log.error("Error returning book: {}", e.getMessage());
            throw new BorrowException("Failed to return book: " + e.getMessage());
        }
    }

    private Borrow checkIfExists(Integer id){
        log.info("inside check if borrow id exists : service");
        return borrowRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.BORROW_NOT_FOUND));
    }

    private void validateDates(LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        LocalDate today = LocalDate.now();

        if (borrowDate.isAfter(today)) {
            throw new IllegalArgumentException("Borrow date cannot be in the future");
        }

        if (dueDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Due date cannot be before borrow date");
        }

        if (returnDate != null) {
            if (returnDate.isBefore(borrowDate)) {
                throw new IllegalArgumentException("Return date cannot be before borrow date");
            }
            if (returnDate.isAfter(today)) {
                throw new IllegalArgumentException("Return date cannot be in the future");
            }
        }
    }


}
