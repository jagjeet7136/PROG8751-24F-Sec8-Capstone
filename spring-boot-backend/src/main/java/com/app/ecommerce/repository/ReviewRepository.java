package com.app.ecommerce.repository;

import com.app.ecommerce.entity.Product;
import com.app.ecommerce.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    List<Review> findByProduct(Product product);
}