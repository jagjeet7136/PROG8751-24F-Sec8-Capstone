package com.app.ecommerce.controller;

import com.app.ecommerce.config.JwtTokenProvider;
import com.app.ecommerce.constants.SecurityConstants;
import com.app.ecommerce.entity.Role;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.exceptions.ForbiddenException;
import com.app.ecommerce.exceptions.NotFoundException;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.*;
import com.app.ecommerce.model.response.JWTLoginSuccessResponse;
import com.app.ecommerce.repository.RoleRepository;
import com.app.ecommerce.repository.UserRepository;
import com.app.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import java.security.Principal;
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

    private final JavaMailSender mailSender;

//    @Value("${spring.mail.username}")
    private String fromEmail = "jagjeet7136@gmail.com"; // From email address

    public UserController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) throws
            ValidationException {
        log.info("Request received for new user creation {}", userCreateRequest);
        User user = userService.createUser(userCreateRequest);
        log.info("User created successfully {}", user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    @GetMapping("/getUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers(@RequestParam(name = "search", required = false) String search) {
        return userService.getUsers(search);
    }

    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createAdmin(@Valid @RequestBody UserCreateRequest userCreateRequest) throws ValidationException {
        log.info("Request received for new admin creation {}", userCreateRequest);
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new ValidationException("Admin role not found"));

        User admin = new User();
        ArrayList<Role> roles = new ArrayList<>();
        admin.setUserFullName(userCreateRequest.getUserFullName().trim());
        userService.usernameAlreadyExists(userCreateRequest.getEmail());
        admin.setUsername(userCreateRequest.getEmail());
        admin.setPassword(bCryptPasswordEncoder.encode(userCreateRequest.getPassword()));
        admin.setRoles(roles);
        admin.getRoles().add(adminRole);

        userRepository.save(admin);
        log.info("Admin created successfully {}", admin);
        return new ResponseEntity<>(admin, HttpStatus.CREATED);
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

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest, Principal principal) throws ValidationException {
        String username = principal.getName(); // Get the username from the Principal object
        User user = userRepository.findByUsername(username);

        // Check if the old password is correct
        if (!bCryptPasswordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            throw new ValidationException("Old password is incorrect");
        }

        // Encode the new password and save it
        String encodedNewPassword = bCryptPasswordEncoder.encode(passwordChangeRequest.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @PostMapping("/verify-email/{email}")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @PathVariable String email) {

        User user = userRepository.findByUsername(email);

        // Assuming user has the securityQuestion field
        String securityQuestion = user.getSecurityQuestion();

        Map<String, String> response = new HashMap<>();
        response.put("securityQuestion", securityQuestion);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws NotFoundException {
        String email = resetPasswordRequest.getEmail();
        User user = userRepository.findByUsername(email);
        if(!Objects.equals(email, user.getUsername())) {
            throw new ForbiddenException("Not Authorised");
        }
        String securityAnswer = resetPasswordRequest.getSecurityAnswer();
        String newPassword = resetPasswordRequest.getNewPassword();

        User userOptional = userRepository.findByUsername(email);

        if (userOptional==null) {
            throw new NotFoundException("User not Found");
        }

        if (!bCryptPasswordEncoder.matches(securityAnswer, user.getSecurityAnswer())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect security answer");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful");
    }


}
