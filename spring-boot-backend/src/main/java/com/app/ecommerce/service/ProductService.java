package com.app.ecommerce.service;

import com.app.ecommerce.entity.Product;
import com.app.ecommerce.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(String productId) {
        return productRepository.findById(Long.parseLong(productId)).get();

    }

    public Page<Product> getProducts(int page, int size, String search) {
        if (search == null || search.trim().isEmpty()) {
            return productRepository.findAll(PageRequest.of(page, size));
        } else {
            return productRepository.findByNameContainingIgnoreCase(search, PageRequest.of(page, size));
        }
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
}
