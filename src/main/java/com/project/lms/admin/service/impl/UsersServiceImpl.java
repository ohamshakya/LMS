package com.project.lms.admin.service.impl;

import com.project.lms.admin.dto.LoginRequest;
import com.project.lms.admin.dto.LoginResponse;
import com.project.lms.admin.dto.UsersDto;
import com.project.lms.admin.dto.UsersResponse;
import com.project.lms.admin.entity.Role;
import com.project.lms.admin.entity.UserPrincipal;
import com.project.lms.admin.entity.Users;
import com.project.lms.admin.mapper.RoleMapper;
import com.project.lms.admin.mapper.UsersMapper;
import com.project.lms.admin.repository.UsersRepo;
import com.project.lms.security.JWTService;
import com.project.lms.admin.service.UsersService;
import com.project.lms.common.exception.AlreadyExistsException;
import com.project.lms.common.exception.ResourceNotFoundException;
import com.project.lms.common.util.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final UsersRepo usersRepo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    public UsersServiceImpl(UsersRepo usersRepo, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.usersRepo = usersRepo;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public String create(UsersDto usersDto) {
        log.info("inside user create : service");
        Users existingusers = usersRepo.findByUsername(usersDto.getUsername());
        if(existingusers != null){
            throw new  AlreadyExistsException(usersDto.getUsername() + " " + Messages.USER_ALREADY_EXISTS);
        }
        Users users = UsersMapper.toEntity(usersDto,encoder);
        Users savedRequest = usersRepo.save(users);
        UsersMapper.toDto(savedRequest);
        return "Users created with the name " + users.getUsername() + " successfully";
    }

    @Override
    public String update(Integer id, UsersDto usersDto) {
        log.info("inside user update : service");
        Users existingUsers = usersRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("NOT FOUND"));
        existingUsers.setFirstName(usersDto.getFirstName());
        existingUsers.setMiddleName(usersDto.getMiddleName());
        existingUsers.setLastName(usersDto.getLastName());
        existingUsers.setPhoneNumber(usersDto.getPhoneNumber());
        existingUsers.setAddress(usersDto.getAddress());
        usersRepo.save(existingUsers);
        return String.format("Users with the id : %d and username : %s",existingUsers.getId(),existingUsers.getUsername());
    }

    @Override
    public UsersDto getById(Integer id) {
        log.info("inside get user by id : service");
        Users usersResponse = usersRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.USER_NOT_FOUND));
        return UsersMapper.toDto(usersResponse);
    }

    @Override
    public Page<UsersResponse> getAll(Pageable pageable) {
        log.info("inside get all with page : service");
        return usersRepo.findAll(pageable).map(UsersMapper::toResponse);
    }

    @Override
    public LoginResponse verify(LoginRequest loginRequest) {
        log.info("inside login verify : service");
        String token = null;
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
      if(authentication.isAuthenticated())
          return LoginResponse.builder()
                  .username(loginRequest.getUsername())
                  .token(jwtService.generateToken(loginRequest.getUsername()))
                  .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                  .build();
     return null;
    }
}
