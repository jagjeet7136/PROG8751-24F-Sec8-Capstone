package com.app.ecommerce.modules.product.api;

import com.app.ecommerce.exceptions.BadRequestException;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductApiImplTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductApiImpl productApi;

    @Test
    void shouldReturnProductInfo_whenProductExists() {

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("iPhone 16")
                .price(BigDecimal.valueOf(999))
                .stock(20)
                .build();

        when(productService.getProduct(1L))
                .thenReturn(response);

        ProductInfo result =
                productApi.getProductInfo(1L);

        assertEquals(1L, result.getId());
        assertEquals("iPhone 16", result.getName());
        assertEquals(BigDecimal.valueOf(999), result.getPrice());
        assertEquals(20, result.getStock());

        verify(productService).getProduct(1L);
    }

    @Test
    void shouldThrowBadRequestException_whenProductIdIsInvalid() {

        assertThrows(
                BadRequestException.class,
                () -> productApi.getProductInfo(0L)
        );

        verify(productService, never())
                .getProduct(anyLong());
    }
}