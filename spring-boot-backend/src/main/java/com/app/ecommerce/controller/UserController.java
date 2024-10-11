package com.app.ecommerce.controller;

import com.app.ecommerce.config.JwtTokenProvider;
import com.app.ecommerce.constants.SecurityConstants;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.exceptions.NotFoundException;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.LoginRequest;
import com.app.ecommerce.model.request.UserCreateRequest;
import com.app.ecommerce.model.response.JWTLoginSuccessResponse;
import com.app.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.security.Principal;

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

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) throws
            ValidationException {
        log.info("Request received for new user creation {}", userCreateRequest);
        User user = userService.createUser(userCreateRequest);
        log.info("User created successfully {}", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

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
        return ResponseEntity.ok(new JWTLoginSuccessResponse(true, token));
    }

}
