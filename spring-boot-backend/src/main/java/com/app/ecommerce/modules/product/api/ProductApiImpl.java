package com.app.ecommerce.modules.product.api;

import com.app.ecommerce.exceptions.BadRequestException;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductApiImpl implements ProductApi {

    private final ProductService productService;

    @Override
    public ProductInfo getProductInfo(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BadRequestException("Invalid product id: {" + productId + "}");
        }
        ProductResponse product = productService.getProduct(productId);

        return mapToProductInfo(product);
    }

    private ProductInfo mapToProductInfo(ProductResponse product) {
        return ProductInfo.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
}
