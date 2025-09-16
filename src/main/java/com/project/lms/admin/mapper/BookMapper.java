package com.project.lms.admin.mapper;

import com.project.lms.admin.dto.BookDto;
import com.project.lms.admin.entity.Book;

public class BookMapper {

    public static Book toEntity(BookDto bookDto){
        return Book.builder()
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .publisher(bookDto.getPublisher())
                .isbn(bookDto.getIsbn())
                .genre(bookDto.getGenre())
                .totalCopies(bookDto.getTotalCopies())
                .availableCopies(bookDto.getAvailableCopies())
                .isAvailable(true)
                .build();
    }

    public static BookDto toDto(Book book){
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .genre(book.getGenre())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .isAvailable(book.getIsAvailable())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
