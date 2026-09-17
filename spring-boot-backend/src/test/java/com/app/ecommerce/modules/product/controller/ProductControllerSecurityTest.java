package com.app.ecommerce.modules.product.controller;

import com.app.ecommerce.modules.product.dto.request.ProductCreateRequest;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.service.ProductService;
import com.app.ecommerce.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ProductControllerSecurityTest.MethodSecurityConfig.class)
class ProductControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @TestConfiguration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    static class MethodSecurityConfig {
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToCreateProduct() throws Exception {

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("iPhone 16")
                .price(BigDecimal.valueOf(999))
                .stock(25)
                .categoryId(1L)
                .build();

        when(productService.createProduct(any(ProductCreateRequest.class)))
                .thenReturn(response);

        String request = """
                {
                  "name": "iPhone 16",
                  "description": "Latest iPhone",
                  "price": 999,
                  "imageUrl": "https://example.com/iphone.jpg",
                  "stock": 25,
                  "categoryId": 1
                }
                """;

        mockMvc.perform(
                        post("/products")
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated());

        verify(productService)
                .createProduct(any(ProductCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidNonAdminFromCreatingProduct() throws Exception {

        String request = """
                {
                  "name": "iPhone 16",
                  "description": "Latest iPhone",
                  "price": 999,
                  "imageUrl": "https://example.com/iphone.jpg",
                  "stock": 25,
                  "categoryId": 1
                }
                """;

        mockMvc.perform(
                        post("/products")
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isForbidden());

        verify(productService, never())
                .createProduct(any(ProductCreateRequest.class));
    }
}