package com.project.lms.admin.mapper;

import com.project.lms.admin.dto.BookDto;
import com.project.lms.admin.dto.DocumentDto;
import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.Document;
import com.project.lms.common.util.FileUploadUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookMapper {

    public static Book toEntity(Book existingBook, BookDto bookDto) {
        if (existingBook == null) {
            existingBook = new Book();
        }
        existingBook.setTitle(bookDto.getTitle());
        existingBook.setAuthor(bookDto.getAuthor());
        existingBook.setPublisher(bookDto.getPublisher());
        existingBook.setIsbn(bookDto.getIsbn());
        existingBook.setGenre(bookDto.getGenre());
        existingBook.setTotalCopies(bookDto.getTotalCopies());
        existingBook.setAuthor(bookDto.getAuthor());
        existingBook.setIsAvailable(true);

        if (existingBook.getDocuments() != null) {
            mapToDocuments(bookDto.getDocuments(), existingBook);
        }

        return existingBook;
    }

    public static BookDto toDto(Book book) {
        if (book == null) return null;
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setIsbn(book.getIsbn());
        dto.setGenre(book.getGenre());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setIsAvailable(book.getIsAvailable());
        dto.setDocuments(book.getDocuments() != null ? book.getDocuments().stream().map(BookMapper::toDocumentDto).toList() : null );
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());

        return dto;
    }

    private static void mapToDocuments(List<DocumentDto> documentDtos, Book existingBook) {
        if (documentDtos == null || documentDtos.isEmpty()) {
            return;
        }

        List<Document> existingDocuments = existingBook.getDocuments();

        Map<String, List<Document>> documentMap = existingDocuments.stream()
                .collect(Collectors.groupingBy(Document::getDocumentType));

        for (DocumentDto documentDto : documentDtos) {
            String documentType = documentDto.getDocumentType();
            String fileNameOrUrl = documentDto.getDocument().getOriginalFilename();

            if (fileNameOrUrl != null && fileNameOrUrl.startsWith("https")) {
                continue;
            }

            fileNameOrUrl = FileUploadUtil.storeFile(documentDto.getDocument());

            if (documentMap.containsKey(documentType)) {
                List<Document> docsOfType = documentMap.get(documentType);

                for (Document existingDocument : docsOfType) {
                    existingDocument.setDoc(fileNameOrUrl);
                }
                continue;
            }
            Document newDocument = Document.builder()
                    .doc(fileNameOrUrl)
                    .documentType(documentType)
                    .book(existingBook)
                    .build();
            existingDocuments.add(newDocument);
        }
    }


    private static DocumentDto toDocumentDto(Document document){
        return DocumentDto.builder()
                .id(document.getId())
                .fileName(document.getDoc())
                .documentType(document.getDocumentType())
                .createdAt(document.getCreatedAt())
                .build();
    }

}
