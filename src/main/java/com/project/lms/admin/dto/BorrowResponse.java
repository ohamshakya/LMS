package com.project.lms.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BorrowResponse {

    private Integer id;

    private LocalDate borrowDate;

    private LocalDate returnDate;

    private String title;

    private String author;

    private String publisher;

    private String isbn;

    private String fullName;

    private String phoneNumber;

    private String address;

    private String userName;

    private LocalDate dueDate;

    private Boolean isReturned;

    private BigDecimal fineAmount;
}
