package com.project.lms.admin.controller;

import com.project.lms.admin.dto.BookDto;
import com.project.lms.admin.dto.DocumentDto;
import com.project.lms.admin.dto.DocumentResponseRequest;
import com.project.lms.admin.dto.TotalBooks;
import com.project.lms.admin.service.BookService;
import com.project.lms.common.exception.FileValidationException;
import com.project.lms.common.util.Messages;
import com.project.lms.common.util.PaginationUtil;
import com.project.lms.common.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/book")
@Slf4j
@Tag(name = "BOOK ",description = "BOOK API FOR LMS")
@CrossOrigin("*")
public class BookController {
    private final BookService bookService;

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String SORT_BY = "updatedAt";
    public static final String SORT_ORDER = "ASC";

//    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_BY = "newest";
    private static final String DEFAULT_SORT_ORDER = "desc";

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseWrapper<BookDto> create(@Valid  @RequestPart("bookDto") BookDto bookDto,
                                           @RequestPart(value = "documents",required = false) List<MultipartFile> documents,
                                           @RequestParam(value = "documentTypes", required = false) List<String> documentTypes) {
        log.info("inside create book : controller");
        List<DocumentDto> documentDtos = validateFile(documents, documentTypes);
        bookDto.setDocuments(documentDtos);

        BookDto response = bookService.create(bookDto);
        return new ResponseWrapper<>(response, Messages.BOOK_CREATED_SUCCESSFULLY, HttpStatus.CREATED.value(),true);
    }


    private List<DocumentDto> validateFile(List<MultipartFile> documents, List<String> documentTypes) {
        if (documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }

        if (documentTypes == null || documentTypes.isEmpty() || documents.size() != documentTypes.size()) {
            throw new FileValidationException("The number of documents does not match the number of document types.");
        }

        List<DocumentDto> documentDtos = new ArrayList<>();
        for (int i = 0; i < documents.size(); i++) {
            DocumentDto documentDto = new DocumentDto();
            documentDto.setDocument(documents.get(i));
            documentDto.setDocumentType(documentTypes.get(i));
            documentDtos.add(documentDto);
        }
        return documentDtos;
    }



    @GetMapping("/{id}")
    public ResponseWrapper<BookDto> getById(@PathVariable Integer id) {
        log.info("inside get by id book : controller");
        BookDto response = bookService.getById(id);
        return new ResponseWrapper<>(response, Messages.BOOK_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value(),true);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<BookDto> update(@PathVariable Integer id, @RequestBody BookDto bookDto){
        log.info("inside update book : controller");
        BookDto response = bookService.update(id, bookDto);
        return new ResponseWrapper<>(response,Messages.BOOK_UPDATED_SUCCESSFULLY,HttpStatus.OK.value(),true);
    }

    @GetMapping("/author")
    public ResponseWrapper<BookDto> getByAuthorName(@RequestParam("authorName")String authorName){
        log.info("inside get author by name : controller");
        BookDto byAuthorName = bookService.getByAuthorName(authorName);
        return new ResponseWrapper<>(byAuthorName,Messages.BOOK_RETRIEVED_BY_AUTHOR_NAME,HttpStatus.OK.value(),true);
    }

    @GetMapping
    public ResponseWrapper<Page<BookDto>> getAllBook(@RequestParam("page")Optional<Integer> page,
                                                     @RequestParam("size")Optional<Integer> size,
                                                     @RequestParam("query")Optional<String> query,
                                                     @RequestParam("sortBy")Optional<String> sortBy,
                                                     @RequestParam("sortOrder")Optional<String> sortOrder){
        log.info("inside get all with pagination : controller");
        Pageable pageable = PaginationUtil.preparePaginationUtil(
                page,
                size.orElse(DEFAULT_PAGE_SIZE),
                sortBy.orElse(SORT_BY),
                sortOrder.orElse(SORT_ORDER)
        );
        Page<BookDto> bookResponse;
        if (query.isPresent() && !query.get().isBlank()) {
            bookResponse = bookService.search(query.get(), pageable);
            return new ResponseWrapper<>(bookResponse, Messages.BOOK_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value(),true);
        } else {
            bookResponse = bookService.getAll(pageable);
            return new ResponseWrapper<>(bookResponse, Messages.BOOK_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value(),true);
        }
    }

    @GetMapping("/total-books")
    public ResponseWrapper<TotalBooks> totalBooks(){
        log.info("inside get total number of books : controller");
        TotalBooks tb = new TotalBooks();
        tb.setTotalBooks(bookService.totalBook());
        tb.setAvailableBooks(bookService.availableBook());

        return new ResponseWrapper<>(tb, Messages.TOTAL_BOOK_RETRIEVED_SUCCESSFULLY,HttpStatus.OK.value(),true);
    }

    @GetMapping("/available-book")
    public ResponseWrapper<List<BookDto>> getAllAvailableBooks(){
        log.info("inside get all available books : controller");
        List<BookDto> allAvailableBook = bookService.getAllAvailableBook();
        return new ResponseWrapper<>(allAvailableBook,Messages.BOOK_RETRIEVED_SUCCESSFULLY,HttpStatus.OK.value(),true);
    }

    @GetMapping("/discover")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<Page<BookDto>> getAllNewestHighRatedMostBorrowed(@RequestParam("page")Optional<Integer> page,
                                                                            @RequestParam("size")Optional<Integer> size,
                                                                            @RequestParam("query")Optional<String> query,
                                                                            @RequestParam("sortBy")Optional<String> sortBy,
                                                                            @RequestParam("sortOrder")Optional<String> sortOrder){
        Pageable pageable = PaginationUtil.preparePaginationUtil(
                page,
                size.orElse(DEFAULT_PAGE_SIZE),
                sortBy.orElse(DEFAULT_SORT_BY),
                sortOrder.orElse(DEFAULT_SORT_ORDER)
        );

        String sortType = sortBy.orElse(DEFAULT_SORT_BY).toLowerCase();
        Page<BookDto> books;

        switch (sortType) {
            case "most_borrowed":
                books = bookService.mostBorrowedBook(pageable);
                break;
            case "highest_rated":
                books = bookService.highestRateBook(pageable);
                break;
            case "newest":
            default:
                books = bookService.newestBook(pageable);
                break;
        }

        return new ResponseWrapper<>(books, "Books retrieved successfully", HttpStatus.OK.value(),true);
    }

    public ResponseWrapper<String> deleteBook(@PathVariable Integer id){
        log.info("inside delete book : controller");
        String deleteBook = bookService.deleteBook(id);
        return new ResponseWrapper<>(deleteBook,"deleted successfully ",HttpStatus.OK.value(),true);
    }

}
