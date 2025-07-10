package com.app.ecommerce.service;

import com.app.ecommerce.entity.Product;
import com.app.ecommerce.entity.Review;
import com.app.ecommerce.entity.ReviewImage;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.model.dto.ReviewDTO;
import com.app.ecommerce.model.response.ReviewResponse;
import com.app.ecommerce.repository.ProductRepository;
import com.app.ecommerce.repository.ReviewImageRepository;
import com.app.ecommerce.repository.ReviewRepository;
import com.app.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewDTO createReview(User user, Long productId, String heading, Integer rating,
                                  String comment, List<MultipartFile> images) throws IOException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        Review review = new Review();
        review.setHeading(heading);
        review.setContent(comment);
        review.setRating(rating);
        review.setUser(user);
        review.setProduct(product);

        List<ReviewImage> savedImages = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                String imageUrl = saveFileAndGetUrl(file);
                ReviewImage reviewImage = new ReviewImage();
                reviewImage.setImageUrl(imageUrl);
                reviewImage.setReview(review);
                savedImages.add(reviewImage);
            }
        }

        review.setImages(savedImages);
        Review savedReview = reviewRepository.save(review);
        log.info("Review saved with id={} for productId={} by user={}", savedReview.getId(), productId, user.getUsername());
        ReviewDTO dto = new ReviewDTO();
        dto.setId(savedReview.getId());
        dto.setRating(savedReview.getRating());
        dto.setComment(savedReview.getContent());
        dto.setUsername(user.getUsername());
        dto.setImageUrls(
                savedReview.getImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList())
        );
        return dto;
    }

    private String saveFileAndGetUrl(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        log.info("Saved review image to path={}", path.toAbsolutePath());
        return "/uploads/" + filename;
    }

    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        log.info("Fetching reviews for productId={}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        List<ReviewResponse> responses = reviewRepository.findByProduct(product).stream()
                .map(r -> new ReviewResponse(
                        r.getUser().getUserFullName(),
                        r.getContent(),
                        r.getRating(),
                        r.getCreatedAt()
                ))
                .collect(Collectors.toList());

        log.info("Fetched {} reviews for productId={}", responses.size(), productId);
        return responses;
    }
}
