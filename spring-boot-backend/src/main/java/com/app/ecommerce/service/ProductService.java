package com.app.ecommerce.service;

import com.app.ecommerce.entity.Category;
import com.app.ecommerce.entity.Product;
import com.app.ecommerce.entity.Review;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.ProductCreateRequest;
import com.app.ecommerce.model.request.ProductUpdateRequest;
import com.app.ecommerce.model.response.ProductResponse;
import com.app.ecommerce.repository.CategoryRepository;
import com.app.ecommerce.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(String productId) {
        Product product = productRepository.findById(Long.parseLong(productId))
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProducts(int page, int size, String search, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        log.debug("Searching products with '{}'", search);
        return search == null || search.trim().isEmpty()
                ? productRepository.findAll(pageable)
                : productRepository.searchProducts(search.trim(), pageable);
    }

    public Product createProduct(ProductCreateRequest request) throws ValidationException {
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElseThrow(() -> new ValidationException("Category not found with name: " + request.getCategoryName()));
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setLongDescription(request.getLongDescription());
        product.setDiscountedPrice(request.getDiscountedPrice());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStock(request.getStock());
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Attempting to update product with ID: {}", id);

        return productRepository.findById(id).map(existingProduct -> {
            log.debug("Found product: {}", existingProduct.getName());

            if (request.getName() != null && !request.getName().isBlank()) {
                log.debug("Updating name to: {}", request.getName());
                existingProduct.setName(request.getName());
            }

            if (request.getDescription() != null && !request.getDescription().isBlank()) {
                log.debug("Updating description.");
                existingProduct.setDescription(request.getDescription());
            }

            if (request.getLongDescription() != null && !request.getLongDescription().isBlank()) {
                log.debug("Updating long description.");
                existingProduct.setLongDescription(request.getLongDescription());
            }

            if (request.getPrice() != null) {
                log.debug("Updating price to: {}", request.getPrice());
                existingProduct.setPrice(request.getPrice());
            }

            if (request.getDiscountedPrice() != null) {
                log.debug("Updating discounted price to: {}", request.getDiscountedPrice());
                existingProduct.setDiscountedPrice(request.getDiscountedPrice());
            }

            if (request.getStock() != null) {
                log.debug("Updating stock to: {}", request.getStock());
                existingProduct.setStock(request.getStock());
            }

            if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
                log.debug("Updating image URL.");
                existingProduct.setImageUrl(request.getImageUrl());
            }

            if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
                log.debug("Updating category to: {}", request.getCategoryName());
                Category category = categoryRepository.findByName(request.getCategoryName()).orElse(null);
                if (category != null) {
                    existingProduct.setCategory(category);
                } else {
                    log.warn("Category not found: {}", request.getCategoryName());
                }
            }

            Product saved = productRepository.save(existingProduct);
            log.info("Product updated successfully: {}", saved.getId());
            return saved;

        }).orElseGet(() -> {
            log.warn("Product with ID {} not found for update.", id);
            return null;
        });
    }


    private ProductResponse mapToResponse(Product product) {
        List<Review> reviews = product.getReviews();
        double averageRating = 0;
        int totalRatings = reviews != null ? reviews.size() : 0;

        if (totalRatings > 0) {
            averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0);
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .longDescription(product.getLongDescription())
                .discountedPrice(product.getDiscountedPrice())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .averageRating(averageRating)
                .totalRatings(totalRatings)
                .build();
    }
}
