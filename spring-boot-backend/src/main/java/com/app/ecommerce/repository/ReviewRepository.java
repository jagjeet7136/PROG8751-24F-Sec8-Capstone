package com.app.ecommerce.repository;

import com.app.ecommerce.entity.Review;
import com.app.ecommerce.model.dto.ProductRatingSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);

    @Query("""
        SELECT
            r.productId AS productId,
            AVG(r.rating) AS averageRating,
            COUNT(r.id) AS totalRatings
        FROM Review r
        GROUP BY r.productId
        """)
    List<ProductRatingSummary> findProductRatingSummaries();
}