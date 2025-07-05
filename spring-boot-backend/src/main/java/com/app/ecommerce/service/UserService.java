package com.app.ecommerce.service;

import com.app.ecommerce.entity.Role;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.entity.VerificationToken;
import com.app.ecommerce.enums.AccountStatus;
import com.app.ecommerce.exceptions.BadRequestException;
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
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public String createUser(UserCreateRequest request) throws ValidationException, BadRequestException, NotFoundException {
        log.debug("Creating user for email: {}", request.getEmail());
        Boolean exists = usernameAlreadyExists(request.getEmail());
        User user;
        if (exists) {
            user = userRepository.findByUsername(request.getEmail());
            if (user.getAccountStatus() != AccountStatus.ACTIVE &&
                    user.getAccountStatus() != AccountStatus.DISABLED_BY_ADMIN) {
                sendGridEmailService.sendUserVerificationEmail(user);
                throw new BadRequestException(user.getUsername() + " already exists, Please verify your email");
            }
            else if (user.getAccountStatus() == AccountStatus.DISABLED_BY_ADMIN) {
                throw new BadRequestException("Account disabled, Please contact customer service team");
            }
            else {
                throw new BadRequestException(user.getUsername() + " already exists");
            }
        } else {
            Role role = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseThrow(() -> new NotFoundException("Default role CUSTOMER not found"));
            user = new User();
            user.setUserFullName(request.getUserFullName().trim());
            user.setUsername(request.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
            user.setRoles(new ArrayList<>(List.of(role)));
            user = userRepository.save(user);
            sendGridEmailService.sendUserVerificationEmail(user);
            log.info("New user created: {}", user.getUsername());
            return "User successfully created, Please verify your email";
        }
    }

    public User getUser(String username, String savedUser) throws NotFoundException {
        if (!username.equals(savedUser)) {
            log.warn("User {} tried to access data of {}", savedUser, username);
            throw new ForbiddenException("Cannot get the user: " + username);
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("User not found: {}", username);
            throw new NotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public Boolean usernameAlreadyExists(String username) {
        return username != null && !username.isBlank() && userRepository.existsByUsername(username);
    }

    public User getLoggedInUser(Principal principal) {
        return (User) ((Authentication) principal).getPrincipal();
    }

    public List<User> getUsers(String search) {
        Pageable pageable = PageRequest.of(0, 20);
        return (search != null && !search.isEmpty())
                ? userRepository.findByUserFullNameContainingOrUsernameContaining(search, search)
                : userRepository.findAll(pageable).getContent();
    }

    public void resetPassword(String token, String newPassword)
            throws ValidationException, BadRequestException {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(token);
        if (optionalToken.isEmpty() || optionalToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Invalid/expired reset token: {}", token);
            throw new BadRequestException("Token is invalid or expired.");
        }
        if (newPassword.isBlank()){
            throw new ValidationException("Password cannot be blank");
        }
        if (newPassword.length() < 6) {
            throw new ValidationException("Password should be at least 6 characters long!");
        }

        VerificationToken verificationToken = optionalToken.get();
        User user = verificationToken.getUser();

        if (bCryptPasswordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("New password should not match with old password");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        try {
            verificationTokenRepository.deleteById(verificationToken.getId());
        } catch (Exception e) {
            log.warn("Token already deleted by other thread: {}", token);
        }
    }

    public void verifyEmail(String token) throws BadRequestException {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            log.warn("Verification token not found: {}", token);
            throw new BadRequestException("Invalid Verification Attempt");
        }

        VerificationToken verificationToken = optionalToken.get();
        User user = verificationToken.getUser();

        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            throw new BadRequestException("Account already verified.");
        }

        if (user.getAccountStatus() != AccountStatus.DISABLED_BY_ADMIN) {
            user.setAccountStatus(AccountStatus.ACTIVE);
            userRepository.save(user);
        } else {
            throw new BadRequestException("Account disabled, Please contact Customer Service");
        }

        try {
            verificationTokenRepository.deleteById(verificationToken.getId());
        } catch (Exception e) {
            log.warn("Token already deleted: {}", token);
        }
    }
}

