package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.modules.product.dto.request.ProductCreateRequest;
import com.app.ecommerce.modules.product.dto.request.ProductSearchCriteriaRequest;
import com.app.ecommerce.modules.product.dto.request.ProductUpdateRequest;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductResponse getProduct(Long productId);

    Page<ProductResponse> getProducts(ProductSearchCriteriaRequest request);

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
}
