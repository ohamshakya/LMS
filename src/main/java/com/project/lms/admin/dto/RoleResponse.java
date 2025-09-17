package com.project.lms.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RoleResponse {
    private Integer id;

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
