package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.config.CacheProperties;
import com.app.ecommerce.entity.Category;
import com.app.ecommerce.exceptions.NotFoundException;
import com.app.ecommerce.modules.product.cache.ProductCacheService;
import com.app.ecommerce.modules.product.domain.entity.Product;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.repository.ProductRepository;
import com.app.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        when(productCacheService.getCachedProduct(productId)).thenReturn(cachedResponse);

        ProductResponse result = productService.getProduct(productId);

        assertEquals("iPhone 16", result.getName());

        verify(productRepository, never()).findById(anyLong());
        verify(productCacheService, never()).cacheProduct(anyLong(), any(ProductResponse.class), any());
    }

    @Test
    void shouldFetchProductFromDatabase_whenCacheMiss() {
        Category category = new Category();
        category.setId(1L);

        Duration productTtl = Duration.ofHours(24);
        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone 16");
        product.setDescription("Latest iPhone");
        product.setPrice(BigDecimal.valueOf(999));
        product.setStock(25);
        product.setCategory(category);

        when(cacheProperties.isEnabled()).thenReturn(true);
        when(productCacheService.getCachedProduct(1L)).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cacheProperties.getProductTtl()).thenReturn(productTtl);

        ProductResponse response = productService.getProduct(1L);

        assertEquals(1L, response.getId());
        assertEquals("iPhone 16", response.getName());
        assertEquals(1L, response.getCategoryId());
        assertEquals(BigDecimal.valueOf(999), response.getPrice());

        verify(productCacheService).getCachedProduct(1L);
        verify(productRepository).findById(1L);
        verify(productCacheService).cacheProduct(eq(1L), any(ProductResponse.class), eq(productTtl));
    }

    @Test
    void shouldFetchProductFromDatabase_whenCacheIsDisabled() {
        Category category = new Category();
        category.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone 16");
        product.setDescription("Latest iPhone");
        product.setPrice(BigDecimal.valueOf(999));
        product.setStock(25);
        product.setCategory(category);

        when(cacheProperties.isEnabled()).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse productResponse = productService.getProduct(1L);

        assertEquals(1L, productResponse.getId());
        assertEquals("iPhone 16", productResponse.getName());
        assertEquals(1L, productResponse.getCategoryId());
        assertEquals(BigDecimal.valueOf(999), productResponse.getPrice());

        verify(productCacheService, never()).getCachedProduct(1L);
        verify(productRepository).findById(1L);
        verify(productCacheService, never()).cacheProduct(eq(1L), any(ProductResponse.class),
                eq(Duration.ofMinutes(1440)));
    }

    @Test
    void shouldThrowNotFoundException_whenProductDoesNotExist() {

        when(cacheProperties.isEnabled()).thenReturn(true);
        when(productCacheService.getCachedProduct(1L)).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, ()->productService.getProduct(1L));

        assertEquals("Product not found with ID: 1", exception.getMessage());

        verify(productCacheService).getCachedProduct(1L);
        verify(productRepository).findById(1L);
        verify(productCacheService, never()).cacheProduct(anyLong(), any(ProductResponse.class), any(Duration.class));
    }
}