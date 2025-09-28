package com.project.lms.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsersDto {


    private Integer id;

    @NotBlank(message = "firstname is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "lastname is required")
    private String lastName;

    @Pattern(regexp = "^\\d{10}$", message = "Phone must be 10 digits")
    private String phoneNumber;

    private String address;

//    @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$", message = "Username must be 5–15 alphanumeric characters")
    private String username;

    @Email
    private String email;

//    @Pattern(
//            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
//            message = "Password must be strong"
//    )
    private String password;


    private Set<RoleDto> roles = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
