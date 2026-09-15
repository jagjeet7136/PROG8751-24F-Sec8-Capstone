package com.app.ecommerce.modules.product.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCacheServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private ProductCacheServiceImpl productCacheService;

    @BeforeEach
    void setUp() {
        productCacheService =
                new ProductCacheServiceImpl(
                        redisTemplate,
                        objectMapper
                );
    }

    @Test
    void shouldNotThrowException_whenProductEvictionFails() {

        Long productId = 1L;
        String key = ProductCacheKeys.product(productId);

        when(redisTemplate.delete(key))
                .thenThrow(new RuntimeException("Redis unavailable"));

        assertDoesNotThrow(
                () -> productCacheService.evictProduct(productId)
        );
    }

    @Test
    void shouldNotThrowException_whenProductListEvictionFails() {

        when(redisTemplate.delete(ProductCacheKeys.ALL_PRODUCTS))
                .thenThrow(new RuntimeException("Redis unavailable"));

        assertDoesNotThrow(
                () -> productCacheService.evictProductList()
        );
    }

    @Test
    void shouldReturnNull_whenRedisReadFails() {

        Long productId = 1L;
        String key = ProductCacheKeys.product(productId);

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(key))
                .thenThrow(new RuntimeException("Redis unavailable"));

        assertNull(
                productCacheService.getCachedProduct(productId)
        );
    }
}