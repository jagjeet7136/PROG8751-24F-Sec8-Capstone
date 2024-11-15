package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Product;
import com.app.ecommerce.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
            @RequestParam(defaultValue = "") String search) {
        Page<Product> products = productService.getProducts(page, size, search);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.createProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }
}
