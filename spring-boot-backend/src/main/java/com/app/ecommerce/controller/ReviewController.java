package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Review;
import com.app.ecommerce.entity.ReviewImage;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.model.dto.ReviewDTO;
import com.app.ecommerce.model.request.ReviewRequest;
import com.app.ecommerce.model.response.ReviewResponse;
import com.app.ecommerce.service.ReviewService;
import com.app.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    private UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addReview(
            @RequestParam("productId") Long productId,
            @RequestParam("heading") String heading,
            @RequestParam("rating") Integer rating,
            @RequestParam("comment") String comment,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Principal principal
    ) throws IOException {
        User user = userService.getLoggedInUser(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not found");
        }
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(productId);
        reviewRequest.setContent(comment);
        reviewRequest.setRating(rating);
        reviewRequest.setHeading(heading);
        reviewRequest.setImages(images);
        Review savedReview = reviewService.createReview(user.getUsername(), reviewRequest);

        ReviewDTO dto = new ReviewDTO();
        dto.setId(savedReview.getId());
        dto.setRating(savedReview.getRating());
        dto.setComment(savedReview.getContent());
        dto.setUsername(savedReview.getUser().getUsername());
        dto.setImageUrls(
                savedReview.getImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList())
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }
}
