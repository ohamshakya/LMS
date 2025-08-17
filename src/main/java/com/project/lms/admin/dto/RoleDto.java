package com.project.lms.admin.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RoleDto {
    private Integer id;

    private String name;

    private Set<UsersDto> users = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
