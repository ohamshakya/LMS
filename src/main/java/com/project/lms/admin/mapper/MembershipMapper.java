package com.project.lms.admin.mapper;

import com.project.lms.admin.dto.MembershipDto;
import com.project.lms.admin.entity.Membership;
import com.project.lms.admin.entity.Users;
import com.project.lms.common.enums.MembershipStatus;

import java.time.LocalDateTime;

public class MembershipMapper {
    public static Membership toEntity(Users users, MembershipDto membershipDto){
        LocalDateTime dateOfIssue = membershipDto.getCreatedAt() != null
                ? membershipDto.getCreatedAt()
                : LocalDateTime.now();

        LocalDateTime expiryDate = dateOfIssue.plusMonths(3);

        MembershipStatus status = expiryDate.isBefore(LocalDateTime.now())
                ? MembershipStatus.EXPIRED
                : MembershipStatus.ACTIVE;

        return Membership.builder()
                .membershipType(membershipDto.getMembershipType())
                .membershipStatus(status)
                .borrowingLimit(5)
                .dateOfIssue(LocalDateTime.now())
                .expiryDate(expiryDate)
                .users(users)
                .build();
    }

    public static MembershipDto toDto(Membership membership){
        return MembershipDto.builder()
                .id(membership.getId())
                .membershipType(membership.getMembershipType())
                .membershipStatus(membership.getMembershipStatus())
                .dateOfIssue(membership.getDateOfIssue())
                .expiryDate(membership.getExpiryDate())
                .borrowingLimit(membership.getBorrowingLimit())
                .updatedAt(membership.getUpdatedAt())
                .build();
    }
}
