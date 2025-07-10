package com.app.ecommerce.controller;

import com.app.ecommerce.entity.User;
import com.app.ecommerce.model.dto.ReviewDTO;
import com.app.ecommerce.model.response.ReviewResponse;
import com.app.ecommerce.service.ReviewService;
import com.app.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addReview(
            @RequestParam("productId") Long productId,
            @RequestParam("heading") String heading,
            @RequestParam("rating") Integer rating,
            @RequestParam("comment") String comment,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Principal principal
    ) throws IOException {
        log.info("POST /reviews - Adding review for productId={} by user={}", productId, principal.getName());
        User user = userService.getLoggedInUser(principal);
        if (user == null) {
            log.warn("Unauthorized attempt to add review by principal={}", principal.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not found");
        }
        ReviewDTO dto = reviewService.createReview(user, productId, heading, rating, comment, images
        );

        log.info("Review created with id={} for productId={} by user={}", dto.getId(), productId, user.getUsername());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long productId) {
        log.info("GET /reviews/product/{} - Fetching reviews", productId);
        List<ReviewResponse> responses = reviewService.getReviewsByProduct(productId);
        log.info("Found {} reviews for productId={}", responses.size(), productId);
        return ResponseEntity.ok(responses);
    }
}
