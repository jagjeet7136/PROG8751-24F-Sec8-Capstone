package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Product;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.ProductCreateRequest;
import com.app.ecommerce.model.request.ProductUpdateRequest;
import com.app.ecommerce.model.response.ProductResponse;
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
    public ResponseEntity<List<ProductResponse>> getHomeProducts() {
        log.info("GET /products - Fetching all products for home");
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId) {
        log.info("GET /products/{} - Fetching product", productId);
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping("/getProducts")
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        log.info("GET /products/getProducts - Page: {}, Size: {}, Search: '{}'", page, size, search);
        return ResponseEntity.ok(productService.getProducts(page, size, search, sortBy, sortOrder));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductCreateRequest productCreateRequest)
            throws ValidationException {
        log.info("POST /products - Creating product: {}", productCreateRequest.getName());
        return new ResponseEntity<>(productService.createProduct(productCreateRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest productUpdateRequest) {
        log.info("PUT /products/{} - Updating product", id);
        Product updatedProduct = productService.updateProduct(id, productUpdateRequest);
        return updatedProduct != null ? ResponseEntity.ok(updatedProduct) : ResponseEntity.notFound().build();
    }
}
