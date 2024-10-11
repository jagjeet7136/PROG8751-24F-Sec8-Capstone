package com.app.ecommerce.service;

import com.app.ecommerce.entity.User;
import com.app.ecommerce.exceptions.ForbiddenException;
import com.app.ecommerce.exceptions.NotFoundException;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.UserCreateRequest;
import com.app.ecommerce.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.Principal;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(UserCreateRequest userCreateRequest) throws ValidationException {
        if(!userCreateRequest.getPassword().trim().equals(userCreateRequest.getConfirmPassword().trim())) {
            throw new ValidationException("passwords do not match");
        }
        usernameAlreadyExists(userCreateRequest.getEmail());
        User newUser = new User();
        newUser.setUserFullName(userCreateRequest.getUserFullName().trim());
        newUser.setUsername(userCreateRequest.getEmail());
        newUser.setPassword(bCryptPasswordEncoder.encode(userCreateRequest.getPassword()));
        return userRepository.save(newUser);
    }

    public User getUser(String username, String savedUser) throws NotFoundException {
        if(!username.equals(savedUser)) {
            throw new ForbiddenException("Cannot get the user : " + username);
        }
        User user = userRepository.findByUsername(username);
        if(user==null) {
            throw new NotFoundException("User not found with username : " + username);
        }
        return user;
    }

    public void usernameAlreadyExists(String username) throws ValidationException {
        if (username!=null && !username.isBlank() && userRepository.existsByUsername(username)) {
            log.error("Username already exists {}", username);
            throw new ValidationException(username.concat(" already exist"));
        }
    }

    public User getLoggedInUser(Principal principal) {
        return (User) ((Authentication) principal).getPrincipal();
    }

}

