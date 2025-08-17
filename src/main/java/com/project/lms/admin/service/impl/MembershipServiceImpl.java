package com.project.lms.admin.service.impl;

import com.project.lms.admin.dto.MembershipDto;
import com.project.lms.admin.entity.Membership;
import com.project.lms.admin.entity.Users;
import com.project.lms.admin.mapper.MembershipMapper;
import com.project.lms.admin.repository.MembershipRepo;
import com.project.lms.admin.repository.UsersRepo;
import com.project.lms.admin.service.MembershipService;
import com.project.lms.common.exception.AlreadyExistsException;
import com.project.lms.common.exception.ResourceNotFoundException;
import com.project.lms.common.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MembershipServiceImpl implements MembershipService {

    private final UsersRepo usersRepo;
    private final MembershipRepo membershipRepo;

    public MembershipServiceImpl(UsersRepo usersRepo, MembershipRepo membershipRepo) {
        this.usersRepo = usersRepo;
        this.membershipRepo = membershipRepo;
    }

    @Override
    public MembershipDto create(Integer id, MembershipDto membershipDto) {
        log.info("inside create membership : service");
        Users usersResponse = usersRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.MEMBERSHIP_NOT_FOUND));
        Membership existingMemberShip = membershipRepo.findByUsersId(usersResponse.getId());
        if(existingMemberShip != null){
            throw new AlreadyExistsException(Messages.MEMBERSHIP_ALREADY_EXISTS);
        }
        Membership membership = MembershipMapper.toEntity(usersResponse,membershipDto);
        membershipRepo.save(membership);
        return MembershipMapper.toDto(membership);
    }

    @Override
    public MembershipDto update(Integer id, MembershipDto membershipDto) {
        log.info("inside update membership : service");
        return null;
    }

    @Override
    public MembershipDto getById(Integer id) {
        log.info("inside get membership by id : service");
        Membership membershipResponse = membershipRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.MEMBERSHIP_NOT_FOUND));
        return MembershipMapper.toDto(membershipResponse);
    }

    @Override
    public Page<MembershipDto> getAll(Pageable pageable) {
        log.info("inside get all membership with page : service");
        return membershipRepo.findAll(pageable).map(MembershipMapper::toDto);
    }

    @Override
    public Integer getTotalMember() {
        log.info("inside get total member : service");
        return membershipRepo.totalMember();
    }
}
