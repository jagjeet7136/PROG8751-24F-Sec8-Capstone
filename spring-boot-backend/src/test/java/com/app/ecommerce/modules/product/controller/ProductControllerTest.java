package com.app.ecommerce.modules.product.controller;

import com.app.ecommerce.exceptions.NotFoundException;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.service.ProductService;
import com.app.ecommerce.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.app.ecommerce.modules.product.dto.request.ProductCreateRequest;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.app.ecommerce.modules.product.dto.request.ProductUpdateRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldReturnProduct_whenProductExists() throws Exception {

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("iPhone 16")
                .description("Latest iPhone")
                .price(BigDecimal.valueOf(999))
                .stock(25)
                .categoryId(1L)
                .build();

        when(productService.getProduct(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/products/{productId}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 16"))
                .andExpect(jsonPath("$.price").value(999))
                .andExpect(jsonPath("$.stock").value(25))
                .andExpect(jsonPath("$.categoryId").value(1));

        verify(productService).getProduct(1L);
    }

    @Test
    void shouldReturnNotFound_whenProductDoesNotExist() throws Exception {

        when(productService.getProduct(999L))
                .thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(
                        get("/products/{productId}", 999L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(productService).getProduct(999L);
    }

    @Test
    void shouldReturnBadRequest_whenCreateProductRequestIsInvalid() throws Exception {

        String invalidRequest = """
            {
              "name": "",
              "description": "",
              "price": -10,
              "imageUrl": "invalid-url",
              "stock": -1
            }
            """;

        mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .createProduct(any(ProductCreateRequest.class));
    }

    @Test
    void shouldCreateProduct_whenRequestIsValid() throws Exception {

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("iPhone 16")
                .description("Latest iPhone")
                .price(BigDecimal.valueOf(999))
                .stock(25)
                .categoryId(1L)
                .build();

        when(productService.createProduct(any(ProductCreateRequest.class)))
                .thenReturn(response);

        String validRequest = """
            {
              "name": "iPhone 16",
              "description": "Latest iPhone",
              "price": 999,
              "imageUrl": "https://example.com/iphone16.jpg",
              "stock": 25,
              "categoryId": 1
            }
            """;

        mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validRequest)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 16"))
                .andExpect(jsonPath("$.price").value(999))
                .andExpect(jsonPath("$.stock").value(25))
                .andExpect(jsonPath("$.categoryId").value(1));

        verify(productService)
                .createProduct(any(ProductCreateRequest.class));
    }

    @Test
    void shouldUpdateProduct_whenRequestIsValid() throws Exception {

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Updated iPhone")
                .description("Updated description")
                .price(BigDecimal.valueOf(1099))
                .stock(30)
                .categoryId(1L)
                .build();

        when(productService.updateProduct(
                eq(1L),
                any(ProductUpdateRequest.class)
        )).thenReturn(response);

        String updateRequest = """
            {
              "name": "Updated iPhone",
              "price": 1099,
              "stock": 30
            }
            """;

        mockMvc.perform(
                        patch("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated iPhone"))
                .andExpect(jsonPath("$.price").value(1099))
                .andExpect(jsonPath("$.stock").value(30));

        verify(productService)
                .updateProduct(
                        eq(1L),
                        any(ProductUpdateRequest.class)
                );
    }

    @Test
    void shouldReturnBadRequest_whenUpdateProductRequestIsInvalid() throws Exception {

        String invalidRequest = """
            {
              "name": "",
              "price": -100,
              "stock": -5
            }
            """;

        mockMvc.perform(
                        patch("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest());

        verify(productService, never())
                .updateProduct(
                        anyLong(),
                        any(ProductUpdateRequest.class)
                );
    }
}