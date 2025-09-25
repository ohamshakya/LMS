package com.project.lms.user.service.impl;

import com.project.lms.admin.entity.Book;
import com.project.lms.admin.entity.UserPrincipal;
import com.project.lms.admin.entity.Users;
import com.project.lms.admin.repository.BookRepo;
import com.project.lms.admin.repository.UsersRepo;
import com.project.lms.common.exception.ResourceNotFoundException;
import com.project.lms.common.util.Messages;
import com.project.lms.user.dto.RatingDto;
import com.project.lms.user.entity.Rating;
import com.project.lms.user.mapper.RatingMapper;
import com.project.lms.user.repository.RatingRepo;
import com.project.lms.user.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepo ratingRepo;
    private final UsersRepo usersRepo;
    private final BookRepo bookRepo;

    public RatingServiceImpl(RatingRepo ratingRepo, UsersRepo usersRepo, BookRepo bookRepo) {
        this.ratingRepo = ratingRepo;
        this.usersRepo = usersRepo;
        this.bookRepo = bookRepo;
    }

    @Override
    public RatingDto create(Integer id,RatingDto ratingDto) {
        log.info("authenticated with id {}",getUserId());
        Users users = usersRepo.findById(getUserId()).orElseThrow(() -> new ResourceNotFoundException("NOT FOUND"));
        Book book = bookRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("NOT FOUND"));
        Rating rating = RatingMapper.toEntity(users,book,ratingDto);
        ratingRepo.save(rating);

        return RatingMapper.toDto(rating);
    }

    private static Integer getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        }

        throw new RuntimeException("Invalid authentication principal");
    }


    @Override
    public RatingDto getById(Integer id) {
        Rating notFound = ratingRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Messages.RATING_NOT_FOUND));
        return RatingMapper.toDto(notFound);
    }

    @Override
    public List<RatingDto> getAll() {
        return ratingRepo.findAll().stream().map(RatingMapper::toDto).collect(Collectors.toList());
    }
}
