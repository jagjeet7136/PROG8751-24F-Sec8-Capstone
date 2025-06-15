package com.app.ecommerce.service;

import com.app.ecommerce.entity.Category;
import com.app.ecommerce.entity.Product;
import com.app.ecommerce.entity.Review;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.ProductCreateRequest;
import com.app.ecommerce.model.response.ProductResponse;
import com.app.ecommerce.repository.CategoryRepository;
import com.app.ecommerce.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        return products.stream().map(product -> {
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
        }).collect(Collectors.toList());
    }

    public ProductResponse getProduct(String productId) {
        Product product = productRepository.findById(Long.parseLong(productId))
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

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

    public Page<Product> getProducts(int page, int size, String search, String sortBy, String sortOrder) {
        log.info("Search Keyword: {}, Page: {}, Size: {}, Sort By: {}, Sort Order: {}", search, page, size, sortBy, sortOrder);
        Sort sort = Sort.by(sortBy);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        if (search == null || search.trim().isEmpty()) {
            return productRepository.findAll(pageable);
        }

        Page<Product> products = productRepository.searchProducts(search.trim(), pageable);
        log.info("products fetched : {}", products);
        return products;
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

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName).orElse(null);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
