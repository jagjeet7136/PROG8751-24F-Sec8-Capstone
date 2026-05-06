package com.app.ecommerce.modules.product.api;

import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.modules.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductApiImpl implements ProductApi {

    private final ProductService productService;

    @Override
    public ProductInfo getProductInfo(Long productId) {
        if(productId==null || productId<=0) {
            throw new ValidationException("Invalid product id: {" + productId + "}");
        }
        var product = productService.getProduct(productId); //may need to change because getProduct returns ProductResponse which is for controller

        return ProductInfo.builder()           //extract to different method if same code is anywhere other
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    @Override
    public void reduceStock(Long productId, int quantity) {
    }
}
