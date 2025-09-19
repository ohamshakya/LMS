package com.project.lms.admin.service;

import com.project.lms.admin.dto.BorrowDto;
import com.project.lms.admin.dto.BorrowResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowService {
    BorrowDto create(BorrowDto borrowDto);

    BorrowDto update(Integer id, BorrowDto borrowDto);

    BorrowDto getById(Integer id);

    Page<BorrowResponse> getAll(Pageable pageable);

    String returnedBook(Integer id);
}
