package com.project.lms.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookResponse {
    private Integer id;

    private String title;

    private String author;

    private String publisher;

    private String isbn;

    private String genre;
}
