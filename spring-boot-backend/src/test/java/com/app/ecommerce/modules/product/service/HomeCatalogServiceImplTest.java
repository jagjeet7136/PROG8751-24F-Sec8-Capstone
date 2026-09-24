package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.entity.Category;
import com.app.ecommerce.model.dto.ProductRatingSummary;
import com.app.ecommerce.modules.product.domain.entity.Product;
import com.app.ecommerce.modules.product.domain.mapper.ProductMapper;
import com.app.ecommerce.modules.product.dto.response.HomeProductsResponse;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.repository.ProductRepository;
import com.app.ecommerce.repository.ReviewRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeCatalogServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Spy
    private ProductMapper productMapper = new ProductMapper();

    @InjectMocks
    private HomeCatalogServiceImpl homeCatalogService;

    @Test
    void shouldBuildHomeProductSections() {

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

        Product ratedProduct = new Product();
        ratedProduct.setId(3L);
        ratedProduct.setName("Highly Rated Product");
        ratedProduct.setPrice(BigDecimal.valueOf(300));
        ratedProduct.setCategory(category);

        ProductRatingSummary ratingSummary =
                mock(ProductRatingSummary.class);

        when(ratingSummary.getProductId())
                .thenReturn(3L);

        when(ratingSummary.getAverageRating())
                .thenReturn(4.8);

        when(ratingSummary.getTotalRatings())
                .thenReturn(25L);

        when(productRepository.findTop8ByOrderByCreatedAtDesc())
                .thenReturn(List.of(newProduct));

        when(productRepository.findExclusiveDeals(any(Pageable.class)))
                .thenReturn(List.of(dealProduct));

        when(reviewRepository.findTopRatedProducts(any(Pageable.class)))
                .thenReturn(List.of(ratingSummary));

        when(productRepository.findAllById(anyList()))
                .thenReturn(List.of(ratedProduct));

        HomeProductsResponse response =
                homeCatalogService.getHomeProducts();

        assertEquals(
                1,
                response.getNewlyReleased().size()
        );

        assertEquals(
                "New Product",
                response.getNewlyReleased()
                        .get(0)
                        .getName()
        );

        assertEquals(
                1,
                response.getExclusiveDeals().size()
        );

        assertEquals(
                "Deal Product",
                response.getExclusiveDeals()
                        .get(0)
                        .getName()
        );

        assertEquals(
                1,
                response.getTopRated().size()
        );

        ProductResponse topRated =
                response.getTopRated().get(0);

        assertEquals(
                "Highly Rated Product",
                topRated.getName()
        );

        assertEquals(
                4.8,
                topRated.getAverageRating()
        );

        assertEquals(
                25,
                topRated.getTotalRatings()
        );

        assertEquals(
                0,
                response.getTopSelling().size()
        );
    }
}