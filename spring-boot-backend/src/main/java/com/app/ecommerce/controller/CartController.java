package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Cart;
import com.app.ecommerce.entity.CartItem;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.repository.UserRepository;
import com.app.ecommerce.service.CartService;
import com.app.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> addProductToCart(
            Principal principal,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        User loggedInUser = userService.getLoggedInUser(principal);
        Cart cart = cartService.addProductToCart(loggedInUser, productId, quantity);
        return new ResponseEntity<>("Product added to cart successfully", HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Cart> removeProductFromCart(
            Principal principal,
            @RequestParam Long productId) {
        User loggedInUser = userService.getLoggedInUser(principal);
        Cart cart = cartService.removeProductFromCart(loggedInUser, productId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping
    public ResponseEntity<Cart> getUserCart(Principal principal) {
        User loggedInUser = userService.getLoggedInUser(principal);
        Cart cart = cartService.getUserCart(loggedInUser);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("getCartItems")
    public ResponseEntity<List<CartItem>> getCartItems(Principal principal) {
        User loggedInUser = userService.getLoggedInUser(principal);
        return new ResponseEntity<>(cartService.getCartItems(loggedInUser), HttpStatus.OK);
    }

}
