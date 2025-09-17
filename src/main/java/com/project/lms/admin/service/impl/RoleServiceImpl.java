package com.project.lms.admin.service.impl;

import com.project.lms.admin.dto.RoleDto;
import com.project.lms.admin.dto.RoleResponse;
import com.project.lms.admin.entity.Role;
import com.project.lms.admin.mapper.RoleMapper;
import com.project.lms.admin.repository.RoleRepo;
import com.project.lms.admin.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;

    public RoleServiceImpl(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        Role role = RoleMapper.toEntity(roleDto);
        roleRepo.save(role);
        return RoleMapper.toDto(role);
    }

    @Override
    public Page<RoleResponse> findAllRole(Pageable pageable) {
      return roleRepo.findAll(pageable).map(RoleMapper::toRoleResponse);
    }

    @Override
    public Page<RoleResponse> search(String keyword, Pageable pageable) {
        return roleRepo.searchRoles(keyword,pageable).map(RoleMapper::toRoleResponse);
    }
}
