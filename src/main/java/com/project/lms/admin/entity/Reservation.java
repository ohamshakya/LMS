package com.project.lms.admin.entity;

import com.project.lms.common.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Membership membership;

    private LocalDate reservationDate;

    private LocalDate notificationDate;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
}
