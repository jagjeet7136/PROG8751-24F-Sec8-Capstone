package com.app.ecommerce.repository;

import com.app.ecommerce.entity.Order;
import com.app.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);
}
