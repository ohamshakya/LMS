package com.project.lms.admin.controller;

import com.project.lms.admin.dto.MembershipDto;
import com.project.lms.admin.service.MembershipService;
import com.project.lms.common.util.Messages;
import com.project.lms.common.util.PaginationUtil;
import com.project.lms.common.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/membership")
@RestController
@Slf4j
@Tag(name = "MEMBERSHIP ",description = "MEMBERSHIP API FOR LMS")
@CrossOrigin("*")
public class MembershipController {
    private final MembershipService membershipService;

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String SORT_BY = "updatedAt";
    public static final String SORT_ORDER = "ASC";

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping("/create/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<MembershipDto> create(@PathVariable Integer id, @RequestBody MembershipDto membershipDto) {
        log.info("inside create membership : controller");
        MembershipDto membershipResponse = membershipService.create(id, membershipDto);
        return new ResponseWrapper<>(membershipResponse, Messages.MEMBERSHIP_CREATED_SUCCESSFULLY, HttpStatus.OK.value(),true);
    }

    @GetMapping("/{id}")
    public ResponseWrapper<MembershipDto> getById(@PathVariable Integer id) {
        log.info("inside get membership by id : controller");
        MembershipDto byId = membershipService.getById(id);
        return new ResponseWrapper<>(byId, Messages.MEMBERSHIP_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value(),true);
    }

    @GetMapping("/user/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<MembershipDto> getByUserId(@PathVariable Integer userId) {
        log.info("inside get membership by user id : controller");
        MembershipDto byUserId = membershipService.getByUserId(userId);
        return new ResponseWrapper<>(byUserId, Messages.MEMBERSHIP_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value(),true);
    }

    @GetMapping
    public ResponseWrapper<Page<MembershipDto>> getAll(@RequestParam("page") Optional<Integer> page,
                                                 @RequestParam("size")Optional<Integer> size,
                                                 @RequestParam("sortBy")Optional<String> sortBy,
                                                 @RequestParam("sortOrder")Optional<String> sortOrder){
        Pageable pageable = PaginationUtil.preparePaginationUtil(
                page,
                size.orElse(DEFAULT_PAGE_SIZE),
                sortBy.orElse(SORT_BY),
                sortOrder.orElse(SORT_ORDER)
        );
        Page<MembershipDto> getAllResponse = membershipService.getAll(pageable);
        return new ResponseWrapper<>(getAllResponse,"retrieved successfully",HttpStatus.OK.value(),true);
    }

    @GetMapping("/total-membership")

    public ResponseWrapper<Integer> totalMembership() {
        log.info("inside total membership : controller");
        Integer response = membershipService.getTotalMember();
        return new ResponseWrapper<>(response, Messages.TOTAL_MEMBERSHIP_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value(),true);
    }

    @DeleteMapping("/{id}")
    public ResponseWrapper<String> deleteMembership(@PathVariable Integer id){
        String delete = membershipService.delete(id);
        return new ResponseWrapper<>(delete,"deleted successfully",HttpStatus.OK.value(),true);
    }

}
