package com.project.lms.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookDto {

    private Integer id;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "author is required")
    private String author;

    @NotBlank(message = "publisher is required")
    private String publisher;

    @NotBlank(message = "isbn is required")
    private String isbn;

    @NotBlank(message = "genre is required")
    private String genre;

    @Positive
    private Integer totalCopies;

    @Positive
    private Integer availableCopies;

    private Boolean isAvailable;

    private List<DocumentDto> documents;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
