package com.app.ecommerce.service;

import com.app.ecommerce.entity.Review;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.model.dto.ReviewDTO;
import com.app.ecommerce.model.response.ReviewResponse;
import com.app.ecommerce.modules.product.api.HomeCatalogApi;
import com.app.ecommerce.modules.product.api.ProductApi;
import com.app.ecommerce.modules.product.api.ProductInfo;
import com.app.ecommerce.repository.ReviewImageRepository;
import com.app.ecommerce.repository.ReviewRepository;
import com.app.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewImageRepository imageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductApi productApi;

    @Mock
    private HomeCatalogApi homeCatalogApi;

    @InjectMocks
    private ReviewService reviewService;


    @Test
    void shouldCreateReviewAndEvictHomeCache_whenReviewIsSaved()
            throws IOException {

        Long productId = 10L;

        User user = new User();
        user.setId(1L);
        user.setUsername("jagjeet");
        user.setUserFullName("Jagjeet Singh");

        ProductInfo productInfo =
                ProductInfo.builder()
                        .id(productId)
                        .name("iPhone 16")
                        .price(BigDecimal.valueOf(999))
                        .stock(20)
                        .build();

        Review savedReview = new Review();
        savedReview.setId(100L);
        savedReview.setHeading("Excellent Product");
        savedReview.setContent("Very good phone");
        savedReview.setRating(5);
        savedReview.setUser(user);
        savedReview.setProductId(productId);
        savedReview.setImages(new ArrayList<>());

        when(productApi.getProductInfo(productId))
                .thenReturn(productInfo);

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(savedReview);

        ReviewDTO result =
                reviewService.createReview(
                        user,
                        productId,
                        "Excellent Product",
                        5,
                        "Very good phone",
                        null
                );

        assertNotNull(result);

        assertEquals(100L, result.getId());
        assertEquals(5, result.getRating());
        assertEquals("Very good phone", result.getComment());
        assertEquals("jagjeet", result.getUsername());
        assertTrue(result.getImageUrls().isEmpty());

        verify(productApi)
                .getProductInfo(productId);

        ArgumentCaptor<Review> reviewCaptor =
                ArgumentCaptor.forClass(Review.class);

        verify(reviewRepository)
                .save(reviewCaptor.capture());

        Review reviewToSave =
                reviewCaptor.getValue();

        assertEquals(
                "Excellent Product",
                reviewToSave.getHeading()
        );

        assertEquals(
                "Very good phone",
                reviewToSave.getContent()
        );

        assertEquals(
                5,
                reviewToSave.getRating()
        );

        assertEquals(
                productId,
                reviewToSave.getProductId()
        );

        assertSame(
                user,
                reviewToSave.getUser()
        );

        assertTrue(
                reviewToSave.getImages().isEmpty()
        );

        verify(homeCatalogApi)
                .evictHomeCache();
    }


    @Test
    void shouldNotEvictHomeCache_whenReviewSaveFails()
            throws IOException {

        Long productId = 10L;

        User user = new User();
        user.setId(1L);
        user.setUsername("jagjeet");

        ProductInfo productInfo =
                ProductInfo.builder()
                        .id(productId)
                        .name("iPhone 16")
                        .price(BigDecimal.valueOf(999))
                        .stock(20)
                        .build();

        when(productApi.getProductInfo(productId))
                .thenReturn(productInfo);

        when(reviewRepository.save(any(Review.class)))
                .thenThrow(
                        new RuntimeException("Database failure")
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reviewService.createReview(
                                user,
                                productId,
                                "Excellent Product",
                                5,
                                "Very good phone",
                                null
                        )
                );

        assertEquals(
                "Database failure",
                exception.getMessage()
        );

        verify(productApi)
                .getProductInfo(productId);

        verify(reviewRepository)
                .save(any(Review.class));

        verify(homeCatalogApi, never())
                .evictHomeCache();
    }


    @Test
    void shouldNotCreateReview_whenProductDoesNotExist()
            throws IOException {

        Long productId = 999L;

        User user = new User();
        user.setId(1L);
        user.setUsername("jagjeet");

        when(productApi.getProductInfo(productId))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reviewService.createReview(
                                user,
                                productId,
                                "Review",
                                5,
                                "Comment",
                                null
                        )
                );

        assertEquals(
                "Product not found with id: 999",
                exception.getMessage()
        );

        verify(productApi)
                .getProductInfo(productId);

        verify(reviewRepository, never())
                .save(any(Review.class));

        verify(homeCatalogApi, never())
                .evictHomeCache();
    }


    @Test
    void shouldReturnReviewsForProduct() {

        Long productId = 10L;

        User user = new User();
        user.setId(1L);
        user.setUsername("jagjeet");
        user.setUserFullName("Jagjeet Singh");

        LocalDateTime createdAt =
                LocalDateTime.of(
                        2026,
                        9,
                        30,
                        18,
                        30
                );

        Review review = new Review();
        review.setId(100L);
        review.setHeading("Excellent");
        review.setContent("Very good product");
        review.setRating(5);
        review.setUser(user);
        review.setProductId(productId);
        review.setCreatedAt(createdAt);

        when(reviewRepository.findByProductId(productId))
                .thenReturn(List.of(review));

        List<ReviewResponse> responses =
                reviewService.getReviewsByProduct(productId);

        assertEquals(1, responses.size());

        ReviewResponse response =
                responses.get(0);

        assertEquals(
                "Jagjeet Singh",
                response.getUserName()
        );

        assertEquals(
                "Very good product",
                response.getContent()
        );

        assertEquals(
                5,
                response.getRating()
        );

        assertEquals(
                createdAt,
                response.getCreatedAt()
        );

        verify(reviewRepository)
                .findByProductId(productId);
    }
}