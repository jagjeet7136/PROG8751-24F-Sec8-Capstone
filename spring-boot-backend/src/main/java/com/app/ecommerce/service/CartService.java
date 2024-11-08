package com.app.ecommerce.service;

import com.app.ecommerce.entity.*;
import com.app.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    public Cart addProductToCart(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        cart.getItems().add(cartItem);
        return cartRepository.save(cart);
    }

    public Cart removeProductFromCart(User user, Long productId) {
        Cart cart = getOrCreateCart(user);
System.out.println(productId);
        // Find the CartItem with the specified product
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return cartRepository.save(cart);
    }

    public Cart getUserCart(User user) {
        return getOrCreateCart(user);
    }

    public List<CartItem> getCartItems(User user) {
        Cart cart = getUserCart(user);
        return cartItemRepository.findByCartId(cart.getId());
    }
}
