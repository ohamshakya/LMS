package com.project.lms.admin.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsersResponse {
    private String firstName;

    private String middleName;

    private String lastName;

    private String phoneNumber;

    private String address;

    private String email;

    private String username;

    private Set<RoleDto> roles = new HashSet<>();

}
