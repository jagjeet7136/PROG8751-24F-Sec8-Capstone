package com.app.ecommerce.service;

import com.app.ecommerce.entity.Category;
import com.app.ecommerce.entity.Product;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.request.ProductCreateRequest;
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

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(String productId) {
        return productRepository.findById(Long.parseLong(productId)).get();

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
