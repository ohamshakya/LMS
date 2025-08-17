package com.project.lms.admin.dto;

import com.project.lms.common.enums.MembershipStatus;
import com.project.lms.common.enums.MembershipType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MembershipDto {
    private Integer id;

    private MembershipType membershipType;

    private MembershipStatus membershipStatus;

    private LocalDateTime dateOfIssue;

    private LocalDateTime expiryDate;

    private Integer borrowingLimit;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
