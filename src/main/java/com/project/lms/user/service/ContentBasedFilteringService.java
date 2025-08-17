package com.project.lms.user.service;

import com.project.lms.admin.entity.Book;

import java.util.List;

public interface ContentBasedFilteringService {
    List<Book> recommend(Integer userId, int topN);
}
