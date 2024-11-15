package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Order;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.dto.OrderDTO;
import com.app.ecommerce.service.OrderService;
import com.app.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> createOrder(
            @RequestBody OrderDTO orderDTO,
            Principal principal) throws ValidationException {
        User user = userService.getLoggedInUser(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not found");
        }
        orderService.createOrder(orderDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully");
    }

    @GetMapping()
    public ResponseEntity<List<Order>> getOrdersForUser(Principal principal) {
        User user = userService.getLoggedInUser(principal);
        return new ResponseEntity<>(orderService.getOrdersByUser(user), HttpStatus.OK);
    }
}
