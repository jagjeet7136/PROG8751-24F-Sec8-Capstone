package com.app.ecommerce.service;

import com.app.ecommerce.entity.*;
import com.app.ecommerce.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            log.info("No cart found for user: {}. Creating new cart.", user.getUsername());
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    public Cart addProductToCart(User user, Long productId, int quantity) {
        log.debug("Adding product (ID: {}) to cart for user: {} with quantity: {}", productId, user.getUsername(), quantity);
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found", productId);
                    return new RuntimeException("Product not found");
                });

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        cart.getItems().add(cartItem);
        Cart updatedCart = cartRepository.save(cart);
        log.info("Product added to cart for user: {} - productId: {}, quantity: {}", user.getUsername(), productId, quantity);
        return updatedCart;
    }

    public Cart removeProductFromCart(User user, Long productId) {
        log.debug("Removing product (ID: {}) from cart for user: {}", productId, user.getUsername());
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found in cart for user: {}", productId, user.getUsername());
                    return new RuntimeException("Product not found in cart");
                });

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        Cart updatedCart = cartRepository.save(cart);
        log.info("Product removed from cart for user: {} - productId: {}", user.getUsername(), productId);
        return updatedCart;
    }

    public Cart getUserCart(User user) {
        log.debug("Fetching cart for user: {}", user.getUsername());
        return getOrCreateCart(user);
    }

    public List<CartItem> getCartItems(User user) {
        Cart cart = getUserCart(user);
        log.debug("Retrieving cart items for user: {}", user.getUsername());
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        log.info("Retrieved {} cart items for user: {}", items.size(), user.getUsername());
        return items;
    }
}
