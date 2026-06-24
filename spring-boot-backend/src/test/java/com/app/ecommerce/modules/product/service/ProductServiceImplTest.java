package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.config.CacheProperties;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.repository.ProductRepository;
import com.app.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CacheProperties cacheProperties;

    @Mock
    private ProductCacheService productCacheService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void shouldReturnProductFromCache_whenCacheHit() {

        Long productId = 1L;
        ProductResponse cachedResponse =
                ProductResponse.builder()
                        .id(productId)
                        .name("iPhone 16")
                        .build();

        when(cacheProperties.isEnabled()).thenReturn(true);
        when(productCacheService.get(productId)).thenReturn(cachedResponse);
        ProductResponse result = productService.getProduct(productId);
        assertEquals("iPhone 16", result.getName());
        verify(productRepository, never()).findById(anyLong());
        verify(productCacheService, never()).put(anyLong(), any(ProductResponse.class), any());
    }
}