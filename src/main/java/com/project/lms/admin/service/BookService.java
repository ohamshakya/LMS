package com.project.lms.admin.service;

import com.project.lms.admin.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookDto create(BookDto bookDto);

    BookDto getById(Integer id);

    BookDto update(Integer id, BookDto bookDto);

    List<BookDto> getAll();

    BookDto getByAuthorName(String authorName);

    Page<BookDto> getAll(Pageable pageable);

    Integer totalBook();

    Integer availableBook();

    List<BookDto> getAllAvailableBook();

    Page<BookDto> search(String keyword,Pageable pageable);




}
