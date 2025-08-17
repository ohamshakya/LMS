package com.project.lms.admin.mapper;

import com.project.lms.admin.dto.RoleDto;
import com.project.lms.admin.entity.Role;

public class RoleMapper {
    public static Role toEntity(RoleDto roleDto){
        if(roleDto == null) return null;
        return Role.builder()
                .name(roleDto.getName())
                .build();
    }

    public static RoleDto toDto(Role role){
        if(role == null) return null;
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
