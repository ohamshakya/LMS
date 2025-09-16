package com.project.lms.admin.controller;

import com.project.lms.admin.dto.LoginRequest;
import com.project.lms.admin.dto.LoginResponse;
import com.project.lms.admin.dto.UsersDto;
import com.project.lms.admin.dto.UsersResponse;
import com.project.lms.admin.service.UsersService;
import com.project.lms.common.util.Messages;
import com.project.lms.common.util.PaginationUtil;
import com.project.lms.common.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
@Tag(name = "USER API ",description = "USER API FOR LMS")
@CrossOrigin("*")
public class UsersController {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String SORT_BY = "updatedAt";
    public static final String SORT_ORDER = "ASC";

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/home")
    @PreAuthorize("hasRole('ADMIN')")
    public String getHome(HttpServletRequest request){
        return "Welcome to Home page" + request.getSession().getId();
    }

    @PostMapping("/register")
    public ResponseWrapper<String> create(@Valid @RequestBody UsersDto usersDto){
        log.info("inside create users : controller");
        String savedResponse = usersService.create(usersDto);
        return new ResponseWrapper<>(savedResponse,Messages.USER_REGISTERED_SUCCESSFULLY, HttpStatus.CREATED.value());
    }

    @PostMapping("/login")
    public ResponseWrapper<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        log.info("inside login : controller");
        LoginResponse verify = usersService.verify(loginRequest);
        return new ResponseWrapper<>(verify,Messages.USER_LOGGED_IN_SUCCESSFULLY,HttpStatus.OK.value());
    }

    @PreAuthorize("hasRole('ADMIN'')")
    @GetMapping
    public ResponseWrapper<Page<UsersResponse>> getAllUser(@RequestParam("page") Optional<Integer> page,
                                                           @RequestParam("size")Optional<Integer> size,
                                                           @RequestParam("sortBy")Optional<String> sortBy,
                                                           @RequestParam("sortOrder")Optional<String> sortOrder){
        log.info("inside get all user : controller");
        Pageable pageable = PaginationUtil.preparePaginationUtil(
                page,
                size.orElse(DEFAULT_PAGE_SIZE),
                sortBy.orElse(SORT_BY),
                sortOrder.orElse(SORT_ORDER)
        );
        Page<UsersResponse> getAllResponse = usersService.getAll(pageable);
        return new ResponseWrapper<>(getAllResponse, Messages.USER_RETRIEVED_SUCCESSFULLY,HttpStatus.OK.value());
    }
}
