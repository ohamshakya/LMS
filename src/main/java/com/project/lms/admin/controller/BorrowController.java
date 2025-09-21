package com.project.lms.admin.controller;

import com.project.lms.admin.dto.BorrowDto;
import com.project.lms.admin.dto.BorrowResponse;
import com.project.lms.admin.service.BorrowService;
import com.project.lms.common.util.Messages;
import com.project.lms.common.util.PaginationUtil;
import com.project.lms.common.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/borrow")
@Slf4j
@Tag(name = "BORROW ",description = "BORROW API FOR LMS")
@CrossOrigin("*")
public class BorrowController {
    private final BorrowService borrowService;

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String SORT_BY = "updatedAt";
    public static final String SORT_ORDER = "ASC";

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping("/create")
    public ResponseWrapper<BorrowDto> create(@RequestBody BorrowDto borrowDto) {
        log.info("inside create borrow : controller");
        BorrowDto borrowResponse = borrowService.create(borrowDto);
        return new ResponseWrapper<>(borrowResponse, Messages.BORROW_CREATED_SUCCESSFULLY, HttpStatus.OK.value(),true);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<BorrowDto> getById(@PathVariable Integer id) {
        log.info("inside get by id : controller");
        BorrowDto byIdResponse = borrowService.getById(id);
        return new ResponseWrapper<>(byIdResponse, Messages.BORROW_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value(),true);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<BorrowDto> update(@PathVariable Integer id, @RequestBody BorrowDto borrowDto) {
        log.info("inside update borrow  : controller");
        BorrowDto updateResponse = borrowService.update(id, borrowDto);
        return new ResponseWrapper<>(updateResponse, Messages.BORROW_UPDATED_SUCCESSFULLY, HttpStatus.OK.value(),true);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<Page<BorrowResponse>> getAll(@RequestParam("page") Optional<Integer> page,
                                                        @RequestParam("size")Optional<Integer> size,
//                                                        @RequestParam("query")Optional<String> query,
                                                        @RequestParam("sortBy")Optional<String> sortBy,
                                                        @RequestParam("sortOrder")Optional<String> sortOrder) {
        log.info("inside get all borrow list by page : controller");
        Pageable pageable = PaginationUtil.preparePaginationUtil(
                page,
                size.orElse(DEFAULT_PAGE_SIZE),
                sortBy.orElse(SORT_BY),
                sortOrder.orElse(SORT_ORDER)
        );

        Page<BorrowResponse> allResponse = borrowService.getAll(pageable);
        return new ResponseWrapper<>(allResponse,"retrieved successfully",HttpStatus.OK.value(),true);
    }

    @PutMapping("/return/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<String> returnBook(@PathVariable Integer id) {
        log.info("inside return book : controller");
        String response = borrowService.returnedBook(id);
        return new ResponseWrapper<>(response, Messages.BOOK_RETURNED_SUCCESSFULLY, HttpStatus.OK.value(),true);
    }
}
