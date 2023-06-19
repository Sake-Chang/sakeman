package com.sakeman.service;

//import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sakeman.entity.User;
import com.sakeman.repository.UserRepository;


@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailService(UserRepository repository) {
        this.userRepository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(email + "Not Found");
        }
        return new UserDetail(user);
    }
}