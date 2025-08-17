package com.project.lms.admin.mapper;

import com.project.lms.admin.dto.BorrowDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Borrow;

import java.time.LocalDate;

public class BorrowMapper {

    public static Borrow toEntity(Book book, BorrowDto borrowDto){
        LocalDate borrowDate = LocalDate.now();
        int loanPeriodDays = 14;
        LocalDate dueDate = borrowDate.plusDays(loanPeriodDays);
        return Borrow.builder()
                .borrowDate(LocalDate.now())
                .returnDate(null)
                .isReturned(false)
                .dueDate(dueDate)
                .book(book)
                .fineAmount(null)
                .build();
    }

    public static BorrowDto toDto(Borrow borrow){
        return BorrowDto.builder()
                .id(borrow.getId())
                .borrowDate(borrow.getBorrowDate())
                .returnDate(borrow.getReturnDate())
                .isReturned(borrow.getIsReturned())
                .build();
    }
}
