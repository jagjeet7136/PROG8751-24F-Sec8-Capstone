package com.app.ecommerce.repository;

import com.app.ecommerce.entity.OrderItem;
import com.app.ecommerce.model.dto.ProductSalesSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
        SELECT
            oi.productId AS productId,
            SUM(oi.quantity) AS totalQuantitySold
        FROM OrderItem oi
        GROUP BY oi.productId
        ORDER BY SUM(oi.quantity) DESC
        """)
    List<ProductSalesSummary> findTopSellingProducts(Pageable pageable);
}
