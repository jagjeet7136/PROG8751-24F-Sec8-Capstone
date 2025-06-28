package com.app.ecommerce.controller;

import com.app.ecommerce.config.JwtTokenProvider;
import com.app.ecommerce.constants.SecurityConstants;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.entity.VerificationToken;
import com.app.ecommerce.enums.AccountStatus;
import com.app.ecommerce.exceptions.BadRequestException;
import com.app.ecommerce.exceptions.NotFoundException;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.*;
import com.app.ecommerce.model.response.JWTLoginSuccessResponse;
import com.app.ecommerce.repository.RoleRepository;
import com.app.ecommerce.repository.UserRepository;
import com.app.ecommerce.repository.VerificationTokenRepository;
import com.app.ecommerce.service.SendGridEmailService;
import com.app.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private SendGridEmailService sendGridEmailService;


    @PostMapping("/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) throws
            ValidationException, IOException {
        log.info("Request received for new user creation {}", userCreateRequest);
        String userResponse = userService.createUser(userCreateRequest);
        log.info("User created successfully {}", userResponse);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/getUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsers(@RequestParam(name = "search", required = false) String search) {
        return new ResponseEntity<>(userService.getUsers(search), HttpStatus.OK);
    }

//    @PostMapping("/admin/create")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<User> createAdmin(@Valid @RequestBody UserCreateRequest userCreateRequest) throws ValidationException {
//        log.info("Request received for new admin creation {}", userCreateRequest);
//        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
//                .orElseThrow(() -> new ValidationException("Admin role not found"));
//
//        User admin = new User();
//        ArrayList<Role> roles = new ArrayList<>();
//        admin.setUserFullName(userCreateRequest.getUserFullName().trim());
//        userService.usernameAlreadyExists(userCreateRequest.getEmail());
//        admin.setUsername(userCreateRequest.getEmail());
//        admin.setPassword(bCryptPasswordEncoder.encode(userCreateRequest.getPassword()));
//        admin.setRoles(roles);
//        admin.getRoles().add(adminRole);
//
//        userRepository.save(admin);
//        log.info("Admin created successfully {}", admin);
//        return new ResponseEntity<>(admin, HttpStatus.CREATED);
//    }

    @GetMapping("/getUser")
    public ResponseEntity<User> getUser(@NotBlank @RequestParam String username, Principal principal) throws
            NotFoundException {
        log.info("Request received for fetching a user : {}", username);
        User user = userService.getUser(username, principal.getName());
        log.info("User fetched successfully {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.helperGenerateToken(authentication);
        log.info("Token generated: {}", token);
        return new ResponseEntity<>(new JWTLoginSuccessResponse(true, token), HttpStatus.OK);
    }

    @PostMapping("/passwordResetEmailVerification")
    public void passwordResetEmailVerification(
            @RequestParam String email) throws ValidationException {

        User user = userRepository.findByUsername(email);
        if(user==null) {
            throw new ValidationException("Invalid Email Address");
        }
        else if(user.getAccountStatus()!=AccountStatus.ACTIVE) {
            throw new ValidationException("User account is not active: " + user.getAccountStatus().toString());
        }
        sendGridEmailService.sendPasswordResetEmail(user);
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<String> validateResetToken(@RequestParam("token") String token) throws ValidationException {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(token);

        if (optionalToken.isEmpty() || optionalToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Invalid or Expired link");
        }

        return new ResponseEntity<>("Token is valid.", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("password") String newPassword) throws ValidationException,
            BadRequestException {
        userService.resetPassword(token, newPassword);
        return new ResponseEntity<>("Password updated successfully.", HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) throws BadRequestException {
        userService.verifyEmail(token);
        return new ResponseEntity<>("Account verified successfully.", HttpStatus.OK);
    }

}
