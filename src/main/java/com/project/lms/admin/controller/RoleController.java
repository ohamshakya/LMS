package com.project.lms.admin.controller;

import com.project.lms.admin.dto.RoleDto;
import com.project.lms.admin.dto.RoleResponse;
import com.project.lms.admin.dto.UsersResponse;
import com.project.lms.admin.service.RoleService;
import com.project.lms.common.util.Messages;
import com.project.lms.common.util.PaginationUtil;
import com.project.lms.common.util.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/role")
@Slf4j
public class RoleController {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String SORT_BY = "updatedAt";
    public static final String SORT_ORDER = "ASC";

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<RoleDto> create(@RequestBody RoleDto roleDto){
        RoleDto createRole = roleService.createRole(roleDto);
        return new ResponseWrapper<>(createRole,"created successfully", HttpStatus.OK.value());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseWrapper<Object> getAllUser(@RequestParam("page") Optional<Integer> page,
                                                          @RequestParam("size")Optional<Integer> size,
                                                          @RequestParam("query")Optional<String> query,
                                                          @RequestParam("sortBy")Optional<String> sortBy,
                                                          @RequestParam("sortOrder")Optional<String> sortOrder){
        log.info("inside get all user : controller");
        Pageable pageable = PaginationUtil.preparePaginationUtil(
                page,
                size.orElse(DEFAULT_PAGE_SIZE),
                sortBy.orElse(SORT_BY),
                sortOrder.orElse(SORT_ORDER)
        );
        Object roleResponse;
        if (query.isPresent() && !query.get().isBlank()) {
            roleResponse = roleService.search(query.get(), pageable);
            return new ResponseWrapper<>(roleResponse, "Retrieved successfully", HttpStatus.OK.value());
        } else {
            roleResponse = roleService.findAllRole(pageable);
            return new ResponseWrapper<>(roleResponse, Messages.USER_RETRIEVED_SUCCESSFULLY, HttpStatus.OK.value());
        }
    }
}
