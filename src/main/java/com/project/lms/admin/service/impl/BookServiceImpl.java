package com.project.lms.admin.service.impl;

import com.project.lms.admin.dto.BookDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.mapper.BookMapper;
import com.project.lms.admin.repository.BookRepo;
import com.project.lms.admin.service.BookService;
import com.project.lms.common.exception.AlreadyExistsException;
import com.project.lms.common.exception.ResourceNotFoundException;
import com.project.lms.common.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepo bookRepo;

    public BookServiceImpl(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
    public BookDto create(BookDto bookDto) {
        log.info("inside create book : service");
        Book existedIsbn = bookRepo.findByIsbn(bookDto.getIsbn());
        if(existedIsbn != null){
            throw new AlreadyExistsException(Messages.ISBN_NUMBER_ALREADY_EXISTS);
        }
        Book book = BookMapper.toEntity(bookDto);
        bookRepo.save(book);
        return BookMapper.toDto(book);
    }

    @Override
    public BookDto getById(Integer id) {
        log.info("inside get by id book : service");
        Book bookResp = bookRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.BOOK_NOT_FOUND));
        return BookMapper.toDto(bookResp);
    }

    @Override
    public BookDto update(Integer id, BookDto bookDto) {
        log.info("inside update book : service");
        Book existingBook = bookRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.BOOK_NOT_FOUND));
        existingBook.setTitle(bookDto.getTitle());
        existingBook.setAuthor(bookDto.getAuthor());
        existingBook.setPublisher(bookDto.getPublisher());
        existingBook.setIsbn(bookDto.getIsbn());
        existingBook.setGenre(bookDto.getGenre());
        existingBook.setTotalCopies(bookDto.getTotalCopies());
        existingBook.setAvailableCopies(bookDto.getAvailableCopies());
        bookRepo.save(existingBook);
        return BookMapper.toDto(existingBook);
    }

    @Override
    public List<BookDto> getAll() {
        log.info("inside get all book : service");
        return bookRepo.findAll().stream().map(BookMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public BookDto getByAuthorName(String authorName) {
        log.info("inside get by author name : service");
        Book byAuthor = bookRepo.findByAuthor(authorName);
        return BookMapper.toDto(byAuthor);
    }

    @Override
    public Page<BookDto> getAll(Pageable pageable) {
        log.info("inside get all book with page : service");
        return bookRepo.findAll(pageable).map(BookMapper::toDto);
    }

    @Override
    public Integer totalBook() {
        log.info("inside get total book : service");
        return bookRepo.countBooks();
    }

    @Override
    public Integer availableBook() {
        log.info("inside get available book count : service");
        return bookRepo.availableBooks();
    }

    @Override
    public List<BookDto> getAllAvailableBook() {
        log.info("inside get all available book : service");
        return bookRepo.availableBook().stream().map(BookMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<BookDto> search(String keyword, Pageable pageable) {
        return bookRepo.searchByMultipleFields(keyword, pageable).map(BookMapper::toDto);
    }

    @Override
    public Page<BookDto> newestBook(Pageable pageable) {
        return bookRepo.findAllByOrderByCreatedAtDesc(pageable).map(BookMapper::toDto);
    }

    @Override
    public Page<BookDto> highestRateBook(Pageable pageable) {
        return bookRepo.findTopRatedBooks(pageable).map(BookMapper::toDto);
    }

    @Override
    public Page<BookDto> mostBorrowedBook(Pageable pageable) {
        return bookRepo.findMostBorrowedBooks(pageable).map(BookMapper::toDto);
    }


}
