package com.project.lms.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BorrowDto {
    private Integer id;

    @NotBlank(message = "borrow date is required")
    private LocalDate borrowDate;

    @NotBlank(message = "return date is required")
    private LocalDate returnDate;

    private LocalDate dueDate;

    private Boolean isReturned;

    private BigDecimal fineAmount;

    @JsonIgnore
    private BookDto bookDto;
}
