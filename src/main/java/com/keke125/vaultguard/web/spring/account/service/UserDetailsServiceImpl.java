package com.keke125.vaultguard.web.spring.account.service;

import java.util.Optional;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("No user present with " + "username: " + username);
        }
    }

    public UserDetails loadUserByUserUid(String userUid) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUid(userUid);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("No user present with " + "userUid: " + userUid);
        }
    }

}
