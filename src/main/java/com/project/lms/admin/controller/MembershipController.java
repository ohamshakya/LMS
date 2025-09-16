package com.project.lms.admin.controller;

import com.project.lms.admin.dto.MembershipDto;
import com.project.lms.admin.service.MembershipService;
import com.project.lms.common.util.Messages;
import com.project.lms.common.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/membership")
@RestController
@Slf4j
@Tag(name = "MEMBERSHIP ",description = "MEMBERSHIP API FOR LMS")
@CrossOrigin("*")
public class MembershipController {
    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping("/create/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<MembershipDto> create(@PathVariable Integer id, @RequestBody MembershipDto membershipDto) {
        log.info("inside create membership : controller");
        MembershipDto membershipResponse = membershipService.create(id, membershipDto);
        return new ResponseWrapper<>(membershipResponse, Messages.MEMBERSHIP_CREATED_SUCCESSFULLY, HttpStatus.OK.value());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<MembershipDto> getById(@PathVariable Integer id) {
        log.info("inside get membership by id : controller");
        MembershipDto byId = membershipService.getById(id);
        return new ResponseWrapper<>(byId, Messages.MEMBERSHIP_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value());
    }

    @GetMapping("/total-membership")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<Integer> totalMembership() {
        log.info("inside total membership : controller");
        Integer response = membershipService.getTotalMember();
        return new ResponseWrapper<>(response, Messages.TOTAL_MEMBERSHIP_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value());
    }

}
