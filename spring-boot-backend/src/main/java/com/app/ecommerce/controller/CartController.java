package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Cart;
import com.app.ecommerce.entity.CartItem;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.model.response.CartItemResponse;
import com.app.ecommerce.repository.UserRepository;
import com.app.ecommerce.service.CartService;
import com.app.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> addProductToCart(Principal principal,
                                                   @RequestParam Long productId,
                                                   @RequestParam int quantity) {
        log.info("POST /cart - Add product to cart: productId={}, quantity={}, user={}", productId, quantity, principal.getName());
        User loggedInUser = userService.getLoggedInUser(principal);
        Cart cart = cartService.addProductToCart(loggedInUser, productId, quantity);
        log.info("Product added to cart successfully for user: {}", principal.getName());
        return new ResponseEntity<>("Product added to cart successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Cart> removeProductFromCart(Principal principal,
                                                      @PathVariable Long itemId) {
        log.info("DELETE /cart/{} - Remove product from cart by user: {}", itemId, principal.getName());
        User loggedInUser = userService.getLoggedInUser(principal);
        Cart cart = cartService.removeProductFromCart(loggedInUser, itemId);
        log.info("Product with itemId={} removed from cart for user: {}", itemId, principal.getName());
        return ResponseEntity.ok(cart);
    }

    @GetMapping
    public ResponseEntity<Cart> getUserCart(Principal principal) {
        log.debug("GET /cart - Fetching cart for user: {}", principal.getName());
        User loggedInUser = userService.getLoggedInUser(principal);
        Cart cart = cartService.getUserCart(loggedInUser);
        log.info("Cart retrieved successfully for user: {}", principal.getName());
        return ResponseEntity.ok(cart);
    }

    @GetMapping("getCartItems")
    public ResponseEntity<List<CartItemResponse>> getCartItems(Principal principal) {
        User loggedInUser = userService.getLoggedInUser(principal);
        List<CartItemResponse> items = cartService.getCartItems(loggedInUser);
        return ResponseEntity.ok(items);
    }
}
