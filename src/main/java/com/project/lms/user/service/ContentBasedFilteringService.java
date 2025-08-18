package com.project.lms.user.service;

import com.project.lms.admin.dto.BookDto;
import com.project.lms.admin.entity.Book;

import java.util.List;

public interface ContentBasedFilteringService {
    List<BookDto> recommend(Integer userId, int topN);
}
