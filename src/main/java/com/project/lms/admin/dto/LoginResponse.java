package com.project.lms.admin.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponse {

    private Integer id;

    private String username;

    private String token;

    private Set<String> roles = new HashSet<>();
}
