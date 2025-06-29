package com.app.ecommerce.service;

import com.app.ecommerce.entity.Role;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.entity.VerificationToken;
import com.app.ecommerce.enums.AccountStatus;
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

    public String createUser(UserCreateRequest userCreateRequest) throws ValidationException, IOException {
        Boolean isUserExists = usernameAlreadyExists(userCreateRequest.getEmail());
        String userResponse = "Some Error Occur";
        User user = null;
        if(isUserExists) {
            user = userRepository.findByUsername(userCreateRequest.getEmail());
            if(user.getAccountStatus() != AccountStatus.ACTIVE && user.getAccountStatus()!=AccountStatus.DISABLED_BY_ADMIN) {

                sendGridEmailService.sendUserVerificationEmail(user);
                throw new ValidationException(user.getUsername() + " already exists, Please verify your email");
            }
            else if(user.getAccountStatus() == AccountStatus.DISABLED_BY_ADMIN) {
                throw new ValidationException("Account disabled, Please contact customer service team");
            }
            else {
                throw new ValidationException(user.getUsername() + " already exists");
            }
        }
        else {
            Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseThrow(() -> new ValidationException("Default role CUSTOMER not found"));
            user = new User();
            Collection<Role> newUserRoles = new ArrayList<>();
            user.setRoles(newUserRoles);
            user.setUserFullName(userCreateRequest.getUserFullName().trim());
            user.setUsername(userCreateRequest.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(userCreateRequest.getPassword()));
            user.getRoles().add(customerRole);
            user = userRepository.save(user);
            sendGridEmailService.sendUserVerificationEmail(user);
            userResponse = "User successfully created, Please verify your email";
        }

        return userResponse;
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

    public Boolean usernameAlreadyExists(String username) throws ValidationException {
        if (username!=null && !username.isBlank() && userRepository.existsByUsername(username)) {
            log.error("Username already exists {}", username);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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

