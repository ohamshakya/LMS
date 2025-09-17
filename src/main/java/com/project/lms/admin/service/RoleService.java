package com.project.lms.admin.service;

import com.project.lms.admin.dto.RoleDto;
import com.project.lms.admin.dto.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface RoleService {
    RoleDto createRole(RoleDto roleDto);

    Page<RoleResponse> findAllRole(Pageable pageable);

    Page<RoleResponse> search(String keyword,Pageable pageable);
}
