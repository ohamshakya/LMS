package com.project.lms.admin.controller;

import com.project.lms.admin.dto.BorrowDto;
import com.project.lms.admin.service.BorrowService;
import com.project.lms.common.util.Messages;
import com.project.lms.common.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
@Slf4j
@Tag(name = "BORROW ",description = "BORROW API FOR LMS")
@CrossOrigin("*")
public class BorrowController {
    private final BorrowService borrowService;

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String sortBy = "updatedAt";
    public static final String sortOrder = "ASC";

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping("/create/{bookId}")
    public ResponseWrapper<BorrowDto> create(@PathVariable Integer bookId, @RequestBody BorrowDto borrowDto) {
        log.info("inside create borrow : controller");
        BorrowDto borrowResponse = borrowService.create(bookId, borrowDto);
        return new ResponseWrapper<>(borrowResponse, Messages.BORROW_CREATED_SUCCESSFULLY, HttpStatus.OK.value());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<BorrowDto> getById(@PathVariable Integer id) {
        log.info("inside get by id : controller");
        BorrowDto byIdResponse = borrowService.getById(id);
        return new ResponseWrapper<>(byIdResponse, Messages.BORROW_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value());
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<BorrowDto> update(@PathVariable Integer id, @RequestBody BorrowDto borrowDto) {
        log.info("inside update borrow  : controller");
        BorrowDto updateResponse = borrowService.update(id, borrowDto);
        return new ResponseWrapper<>(updateResponse, Messages.BORROW_UPDATED_SUCCESSFULLY, HttpStatus.OK.value());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<List<BorrowDto>> get() {
        log.info("inside get all borrow list : controller");
        return null;
    }

    @PutMapping("/return/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<String> returnBook(@PathVariable Integer id) {
        log.info("inside return book : controller");
        String response = borrowService.returnedBook(id);
        return new ResponseWrapper<>(response, Messages.BOOK_RETURNED_SUCCESSFULLY, HttpStatus.OK.value());
    }
}
