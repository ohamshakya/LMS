package com.project.lms.admin.service.impl;

import com.project.lms.admin.entity.UserPrincipal;
import com.project.lms.admin.entity.Users;
import com.project.lms.admin.repository.UsersRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UsersRepo usersRepo;

    public MyUserDetailsService(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = usersRepo.findByUsername(username);
        if (users == null) {
            throw new UsernameNotFoundException("username doesn't exists");
        }
        return new UserPrincipal(users);
    }
}
