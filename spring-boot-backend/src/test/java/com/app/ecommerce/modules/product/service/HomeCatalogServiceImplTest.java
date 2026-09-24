package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.entity.Category;
import com.app.ecommerce.modules.product.domain.entity.Product;
import com.app.ecommerce.modules.product.domain.mapper.ProductMapper;
import com.app.ecommerce.modules.product.dto.response.HomeProductsResponse;
import com.app.ecommerce.modules.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeCatalogServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ProductMapper productMapper = new ProductMapper();

    @InjectMocks
    private HomeCatalogServiceImpl homeCatalogService;

    @Test
    void shouldBuildProductBasedHomeSections() {

        Category category = new Category();
        category.setId(1L);

        Product newProduct = new Product();
        newProduct.setId(1L);
        newProduct.setName("New Product");
        newProduct.setPrice(BigDecimal.valueOf(100));
        newProduct.setCategory(category);

        Product dealProduct = new Product();
        dealProduct.setId(2L);
        dealProduct.setName("Deal Product");
        dealProduct.setPrice(BigDecimal.valueOf(200));
        dealProduct.setDiscountedPrice(BigDecimal.valueOf(150));
        dealProduct.setCategory(category);

        when(productRepository.findTop8ByOrderByCreatedAtDesc())
                .thenReturn(List.of(newProduct));

        when(productRepository.findExclusiveDeals(any(Pageable.class)))
                .thenReturn(List.of(dealProduct));

        HomeProductsResponse response =
                homeCatalogService.getHomeProducts();

        assertEquals(1, response.getNewlyReleased().size());
        assertEquals("New Product",
                response.getNewlyReleased().get(0).getName());

        assertEquals(1, response.getExclusiveDeals().size());
        assertEquals("Deal Product",
                response.getExclusiveDeals().get(0).getName());

        assertEquals(0, response.getTopSelling().size());
        assertEquals(0, response.getTopRated().size());
    }
}