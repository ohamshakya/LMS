package com.project.lms.admin.service;

import com.project.lms.admin.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsersService {
    String create(UsersDto usersDto);

    String update(Integer id, UsersDto usersDto);

    UsersDto getById(Integer id);

    Page<UsersResponse> getAll(Pageable pageable);

    LoginResponse verify(LoginRequest loginRequest);

    String delete(Integer id);

    Page<UsersResponse> search(String keyword,Pageable pageable);

    TotalNumberResponse getTotalNumbers();
}
