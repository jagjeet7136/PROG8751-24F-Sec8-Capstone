package com.app.ecommerce.modules.product.repository;

import com.app.ecommerce.modules.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN p.category c WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    List<Product> findTop8ByOrderByCreatedAtDesc();

    @Query("""
        SELECT p
        FROM Product p
        WHERE p.discountedPrice IS NOT NULL
          AND p.discountedPrice > 0
          AND p.discountedPrice < p.price
        ORDER BY p.createdAt DESC
        """)
    List<Product> findExclusiveDeals(Pageable pageable);
}
