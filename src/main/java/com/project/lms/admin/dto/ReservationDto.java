package com.project.lms.admin.dto;

import com.project.lms.common.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReservationDto {
    private Integer id;

    private Integer bookId;

    private String bookTitle;

    private Integer memberId;

    private String memberName;

    private String phoneNumber;

    private LocalDate reservationDate;

    private LocalDate notificationDate;

    private LocalDate expiryDate;

    private ReservationStatus status;
}
