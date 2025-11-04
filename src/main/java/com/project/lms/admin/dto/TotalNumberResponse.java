package com.project.lms.admin.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TotalNumberResponse {
    private Integer totalUsers;

    private Integer totalReservation;

    private Integer totalMemberShip;

    private Integer totalBooks;

    private Integer availableBooks;

    private Integer totalBorrows;

    private Integer totalRoles;


}
