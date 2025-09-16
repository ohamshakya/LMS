package com.project.lms.admin.entity;

import com.project.lms.common.enums.MembershipStatus;
import com.project.lms.common.enums.MembershipType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    @Enumerated(EnumType.STRING)
    private MembershipStatus membershipStatus;

    private LocalDateTime dateOfIssue;

    private LocalDateTime expiryDate;

    private Integer borrowingLimit;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Users users;

    @OneToMany(mappedBy = "membership", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

