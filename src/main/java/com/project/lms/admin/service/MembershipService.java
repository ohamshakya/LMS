package com.project.lms.admin.service;

import com.project.lms.admin.dto.MembershipDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MembershipService {
    MembershipDto create(Integer id, MembershipDto membershipDto);

    MembershipDto update(Integer id, MembershipDto membershipDto);

    MembershipDto getById(Integer id);

    Page<MembershipDto> getAll(Pageable pageable);

    Integer getTotalMember();

    String delete(Integer id);
}
