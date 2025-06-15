package com.app.ecommerce.service;

import com.app.ecommerce.entity.Product;
import com.app.ecommerce.entity.Review;
import com.app.ecommerce.entity.ReviewImage;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.model.request.ReviewRequest;
import com.app.ecommerce.model.response.ReviewResponse;
import com.app.ecommerce.repository.ProductRepository;
import com.app.ecommerce.repository.ReviewImageRepository;
import com.app.ecommerce.repository.ReviewRepository;
import com.app.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Review createReview(String username, ReviewRequest request) throws IOException {
        User user = userRepository.findByUsername(username);
        Product product = productRepository.findById(request.getProductId()).orElseThrow();

        Review review = new Review();
        review.setHeading(request.getHeading());
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setUser(user);
        review.setProduct(product);

        List<ReviewImage> savedImages = new ArrayList<>();
        if (request.getImages() != null) {
            for (MultipartFile file : request.getImages()) {
                String imageUrl = saveFileAndGetUrl(file);
                ReviewImage reviewImage = new ReviewImage();
                reviewImage.setImageUrl(imageUrl);
                reviewImage.setReview(review);
                savedImages.add(reviewImage);
            }
        }

        review.setImages(savedImages);
        return reviewRepository.save(review);
    }

    private String saveFileAndGetUrl(MultipartFile file) throws IOException {
        // For demo: Save to local file system
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return "/uploads/" + filename;
    }

    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return reviewRepository.findByProduct(product).stream()
                .map(r -> new ReviewResponse(
                        r.getUser().getUserFullName(),
                        r.getContent(),
                        r.getRating(),
                        r.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
