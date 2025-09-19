package com.project.lms.admin.mapper;

import com.project.lms.admin.dto.BookResponse;
import com.project.lms.admin.dto.BorrowDto;
import com.project.lms.admin.dto.BorrowResponse;
import com.project.lms.admin.dto.UserResponse;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Borrow;
import com.project.lms.admin.entity.Users;

import java.time.LocalDate;

public class BorrowMapper {

    public static Borrow toEntity(Book book, Users user, BorrowDto borrowDto){
        LocalDate borrowDate = LocalDate.now();
        int loanPeriodDays = 14;
        LocalDate dueDate = borrowDate.plusDays(loanPeriodDays);
        return Borrow.builder()
                .borrowDate(LocalDate.now())
                .returnDate(null)
                .isReturned(false)
                .dueDate(dueDate)
                .book(book)
                .user(user)
                .fineAmount(null)
                .build();
    }

    public static BorrowDto toDto(Borrow borrow){
        BookResponse bookResp = BookResponse.builder()
                .id(borrow.getBook().getId())
                .isbn(borrow.getBook().getIsbn())
                .title(borrow.getBook().getTitle())
                .genre(borrow.getBook().getGenre())
                .author(borrow.getBook().getAuthor())
                .publisher(borrow.getBook().getPublisher())
                .build();

        UserResponse userResp = UserResponse.builder()
                .id(borrow.getUser().getId())
                .fullName(borrow.getUser().getFirstName() + " " + borrow.getUser().getLastName())
                .address(borrow.getUser().getAddress())
                .phoneNumber(borrow.getUser().getPhoneNumber())
                .username(borrow.getUser().getPhoneNumber())
                .build();

        return BorrowDto.builder()
                .borrowDate(borrow.getBorrowDate())
                .returnDate(borrow.getReturnDate())
                .books(bookResp)
                .users(userResp)
                .dueDate(borrow.getDueDate())
                .isReturned(borrow.getIsReturned())
                .fineAmount(borrow.getFineAmount())
                .build();
    }

    public static BorrowResponse toResponse(Borrow borrow){
        return BorrowResponse.builder()
                .id(borrow.getId())
                .borrowDate(borrow.getBorrowDate())
                .returnDate(borrow.getReturnDate())
                .fullName(borrow.getUser().getFirstName() + " " + borrow.getUser().getLastName())
                .address(borrow.getUser().getAddress())
                .phoneNumber(borrow.getUser().getPhoneNumber())
                .userName(borrow.getUser().getUsername())
                .isbn(borrow.getBook().getIsbn())
                .title(borrow.getBook().getTitle())
                .author(borrow.getBook().getAuthor())
                .publisher(borrow.getBook().getPublisher())
                .dueDate(borrow.getDueDate())
                .isReturned(borrow.getIsReturned())
                .fineAmount(borrow.getFineAmount())
                .build();

    }
}
