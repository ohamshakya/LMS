package com.project.lms.admin.mapper;

import com.project.lms.admin.dto.UsersDto;
import com.project.lms.admin.dto.UsersResponse;
import com.project.lms.admin.entity.Users;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.stream.Collectors;

public class UsersMapper {
    public static Users toEntity(UsersDto usersDto, BCryptPasswordEncoder encoder){
        return Users.builder()
                .firstName(usersDto.getFirstName())
                .middleName(usersDto.getMiddleName())
                .lastName(usersDto.getLastName())
                .phoneNumber(usersDto.getPhoneNumber())
                .address(usersDto.getAddress())
                .username(usersDto.getUsername())
                .email(usersDto.getEmail())
                .password(encoder.encode(usersDto.getPassword()))
                .roles(usersDto.getRoles() == null ? null : usersDto.getRoles().stream()
                        .map(RoleMapper::toEntity)
                        .collect(Collectors.toSet())
                )
                .build();
    }

    public static UsersDto toDto(Users users){
        return UsersDto.builder()
                .id(users.getId())
                .firstName(users.getFirstName())
                .middleName(users.getMiddleName())
                .lastName(users.getLastName())
                .phoneNumber(users.getPhoneNumber())
                .address(users.getAddress())
                .username(users.getUsername())
                .roles(users.getRoles() == null ? null : users.getRoles().stream().map(RoleMapper::toDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static UsersResponse toResponse(Users users){
        return UsersResponse.builder()
                .firstName(users.getFirstName())
                .middleName(users.getMiddleName())
                .lastName(users.getLastName())
                .phoneNumber(users.getPhoneNumber())
                .address(users.getAddress())
                .username(users.getUsername())
                .email(users.getEmail())
                .roles(users.getRoles() == null ? null : users.getRoles().stream().map(RoleMapper::toDto).collect(Collectors.toSet()))
                .build();
    }
}
