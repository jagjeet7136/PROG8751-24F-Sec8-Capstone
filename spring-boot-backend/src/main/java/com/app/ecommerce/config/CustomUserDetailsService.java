package com.app.ecommerce.config;

import com.app.ecommerce.entity.User;
import com.app.ecommerce.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user==null) {
            log.error("Username not found {}", username);
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Transactional
    public User loadUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user==null) {
            log.error("User not found {}", id);
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
