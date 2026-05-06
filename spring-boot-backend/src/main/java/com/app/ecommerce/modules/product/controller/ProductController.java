package com.app.ecommerce.modules.product.controller;

import com.app.ecommerce.modules.product.dto.request.ProductCreateRequest;
import com.app.ecommerce.modules.product.dto.request.ProductSearchCriteriaRequest;
import com.app.ecommerce.modules.product.dto.request.ProductUpdateRequest;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getHomeProducts() {
        log.info("GET /products - Fetching all products for home");
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        log.info("GET /products/{} - Fetching product", productId);
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @Valid @ModelAttribute ProductSearchCriteriaRequest request) {
        log.info("GET /products/getProducts - Fetching searched products : {}", request);
        return ResponseEntity.ok(productService.getProducts(request));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest productCreateRequest) {
        log.info("POST /products - Creating product: {}", productCreateRequest.getName());
        return new ResponseEntity<>(productService.createProduct(productCreateRequest), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest productUpdateRequest) {
        log.info("PUT /products/{} - Updating product", id);
        ProductResponse updatedProduct = productService.updateProduct(id, productUpdateRequest);
        return updatedProduct != null ? ResponseEntity.ok(updatedProduct) : ResponseEntity.notFound().build();
    }
}
