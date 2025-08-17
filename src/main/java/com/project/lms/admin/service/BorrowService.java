package com.project.lms.admin.service;

import com.project.lms.admin.dto.BorrowDto;

public interface BorrowService {
    BorrowDto create(Integer id, BorrowDto borrowDto);

    BorrowDto update(Integer id, BorrowDto borrowDto);

    BorrowDto getById(Integer id);

//    Page<BorrowDto> getAll(Pageable pageable);

    String returnedBook(Integer id);
}
