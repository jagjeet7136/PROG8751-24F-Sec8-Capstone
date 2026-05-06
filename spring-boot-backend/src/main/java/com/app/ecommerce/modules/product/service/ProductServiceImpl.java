package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.entity.Category;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.modules.product.domain.entity.Product;
import com.app.ecommerce.modules.product.dto.request.ProductSearchCriteriaRequest;
import com.app.ecommerce.modules.product.dto.request.ProductCreateRequest;
import com.app.ecommerce.modules.product.dto.request.ProductUpdateRequest;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.repository.ProductRepository;
import com.app.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found with ID: " + productId));
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(ProductSearchCriteriaRequest criteria) {
        Sort sort = Sort.by(
                Sort.Direction.fromString(criteria.getSortOrder()),
                criteria.getSortBy()
        );
        Pageable pageable = PageRequest.of(
                criteria.getPage(),
                criteria.getSize(),
                sort
        );
        String search = criteria.getSearch();
        log.debug("Searching products with '{}'", search);
        Page<Product> productPage =
                (search == null || search.trim().isEmpty())
                        ? productRepository.findAll(pageable)
                        : productRepository.searchProducts(search.trim(), pageable);
        return productPage.map(this::mapToResponse);
    }

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) throws ValidationException {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ValidationException("Category not found with Id: " + request.getCategoryId()));
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription().trim());
        product.setLongDescription(request.getLongDescription());
        product.setDiscountedPrice(request.getDiscountedPrice());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStock(request.getStock());
        product.setCategory(category);
        return mapToResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Attempting to update product with ID: {}", id);

        return productRepository.findById(id).map(existingProduct -> {
            log.debug("Found product: {}", existingProduct);

            if (request.getName() != null && !request.getName().isBlank()) {
                existingProduct.setName(request.getName());
            }

            if (request.getDescription() != null && !request.getDescription().isBlank()) {
                existingProduct.setDescription(request.getDescription());
            }

            if (request.getLongDescription() != null && !request.getLongDescription().isBlank()) {
                existingProduct.setLongDescription(request.getLongDescription());
            }

            if (request.getPrice() != null) {
                existingProduct.setPrice(request.getPrice());
            }

            if (request.getDiscountedPrice() != null) {
                existingProduct.setDiscountedPrice(request.getDiscountedPrice());
            }

            if (request.getStock() != null) {
                existingProduct.setStock(request.getStock());
            }

            if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
                existingProduct.setImageUrl(request.getImageUrl());
            }

            if (request.getCategoryId() != null) {
                categoryRepository.findById(request.getCategoryId()).ifPresent(existingProduct::setCategory);
            }
            ProductResponse savedProduct = mapToResponse(productRepository.save(existingProduct));
            log.info("Product updated successfully: {}", savedProduct.getId());
            return savedProduct;

        }).orElseThrow(() ->
            new ValidationException("Product not found with ID: " + id));
    }

    private ProductResponse mapToResponse(Product product) {
//        List<Review> reviews = product.getReviews();
//        double averageRating = 0;
//        int totalRatings = reviews != null ? reviews.size() : 0;
//
//        if (totalRatings > 0) {
//            averageRating = reviews.stream()
//                    .mapToInt(Review::getRating)
//                    .average()
//                    .orElse(0);
//        }
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
//                .averageRating(averageRating)
//                .totalRatings(totalRatings)
                .build();
    }
}
