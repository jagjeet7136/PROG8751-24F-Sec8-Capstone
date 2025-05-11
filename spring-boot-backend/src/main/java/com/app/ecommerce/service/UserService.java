package com.app.ecommerce.service;

import com.app.ecommerce.entity.Role;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.entity.VerificationToken;
import com.app.ecommerce.exceptions.ForbiddenException;
import com.app.ecommerce.exceptions.NotFoundException;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.UserCreateRequest;
import com.app.ecommerce.repository.RoleRepository;
import com.app.ecommerce.repository.UserRepository;
import com.app.ecommerce.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    SendGridEmailService sendGridEmailService;

    public User createUser(UserCreateRequest userCreateRequest) throws ValidationException, IOException {
        if (!userCreateRequest.getPassword().trim().equals(userCreateRequest.getConfirmPassword().trim())) {
            throw new ValidationException("Passwords do not match");
        }
        usernameAlreadyExists(userCreateRequest.getEmail());
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ValidationException("Default role CUSTOMER not found"));
        User newUser = new User();
        Collection<Role> newUserRoles = new ArrayList<>();
        newUser.setRoles(newUserRoles);
        newUser.setUserFullName(userCreateRequest.getUserFullName().trim());
        newUser.setUsername(userCreateRequest.getEmail());
        newUser.setSecurityQuestion(userCreateRequest.getSecurityQuestion().trim());
        newUser.setSecurityAnswer(bCryptPasswordEncoder.encode(userCreateRequest.getSecurityAnswer().trim()));
        newUser.setPassword(bCryptPasswordEncoder.encode(userCreateRequest.getPassword()));
        newUser.getRoles().add(customerRole);
        User user = userRepository.save(newUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(newUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24-hour validity
        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:3000/verify-email?token=" + token;
        String emailBody = "Click to verify your email: " + verificationLink;
        sendGridEmailService.sendEmail(newUser.getUsername(), "Email Verification", emailBody);

        return newUser;
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

    public List<User> getUsers(String search) {
        Pageable pageable = PageRequest.of(0, 20);

        if (search != null && !search.isEmpty()) {
            return userRepository.findByUserFullNameContainingOrUsernameContaining(search, search);
        } else {
            return userRepository.findAll(pageable).getContent();
        }
    }

}

