package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Product;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.ProductCreateRequest;
import com.app.ecommerce.model.request.ProductUpdateRequest;
import com.app.ecommerce.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
@Slf4j
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getHomeProducts() {
        log.info("ALl product requested");
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable String productId) {
        log.info("Product requested with ID: " + productId);
        return new ResponseEntity<>(productService.getProduct(productId), HttpStatus.OK);
    }

    @GetMapping("/getProducts")
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        Page<Product> products = productService.getProducts(page, size, search, sortBy, sortOrder);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductCreateRequest productCreateRequest) throws
            ValidationException {
        Product createdProduct = productService.createProduct(productCreateRequest);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest productUpdateRequest) {

        Product existingProduct = productService.getProductById(id);

        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        if (productUpdateRequest.getName() != null && !productUpdateRequest.getName().trim().isEmpty()) {
            existingProduct.setName(productUpdateRequest.getName());
        }

        if (productUpdateRequest.getDescription() != null && !productUpdateRequest.getDescription().trim().isEmpty()) {
            existingProduct.setDescription(productUpdateRequest.getDescription());
        }

        if (productUpdateRequest.getLongDescription() != null && !productUpdateRequest.getLongDescription().trim().isEmpty()) {
            existingProduct.setLongDescription(productUpdateRequest.getLongDescription());
        }

        if (productUpdateRequest.getPrice() != null) {
            existingProduct.setPrice(productUpdateRequest.getPrice());
        }

        if (productUpdateRequest.getDiscountedPrice() != null) {
            existingProduct.setDiscountedPrice(productUpdateRequest.getDiscountedPrice());
        }

        if (productUpdateRequest.getStock() != null) {
            existingProduct.setStock(productUpdateRequest.getStock());
        }

        if (productUpdateRequest.getImageUrl() != null && !productUpdateRequest.getImageUrl().trim().isEmpty()) {
            existingProduct.setImageUrl(productUpdateRequest.getImageUrl());
        }

        if (productUpdateRequest.getCategoryName() != null && !productUpdateRequest.getCategoryName().trim().isEmpty()) {
            existingProduct.setCategory(productService.getCategoryByName(productUpdateRequest.getCategoryName()));
        }

        Product updatedProduct = productService.saveProduct(existingProduct);

        return ResponseEntity.ok(updatedProduct);
    }
}
