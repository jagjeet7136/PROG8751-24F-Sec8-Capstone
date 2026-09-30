package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.config.CacheProperties;
import com.app.ecommerce.model.dto.ProductRatingSummary;
import com.app.ecommerce.modules.product.cache.ProductCacheService;
import com.app.ecommerce.modules.product.domain.entity.Product;
import com.app.ecommerce.modules.product.domain.mapper.ProductMapper;
import com.app.ecommerce.modules.product.dto.response.HomeProductsResponse;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.repository.ProductRepository;
import com.app.ecommerce.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.app.ecommerce.model.dto.ProductSalesSummary;
import com.app.ecommerce.repository.OrderItemRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeCatalogServiceImpl implements HomeCatalogService {

    private static final int SECTION_SIZE = 8;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductMapper productMapper;
    private final ProductCacheService productCacheService;
    private final CacheProperties cacheProperties;

    @Override
    @Transactional(readOnly = true)
    public HomeProductsResponse getHomeProducts() {

        if (cacheProperties.isEnabled()) {

            HomeProductsResponse cached =
                    productCacheService.getCachedHomeProducts();

            if (cached != null) {
                return cached;
            }
        }

        List<ProductResponse> newlyReleased =
                productRepository.findTop8ByOrderByCreatedAtDesc()
                        .stream()
                        .map(productMapper::toResponse)
                        .collect(Collectors.toList());

        List<ProductResponse> exclusiveDeals =
                productRepository.findExclusiveDeals(
                                PageRequest.of(0, SECTION_SIZE)
                        )
                        .stream()
                        .map(productMapper::toResponse)
                        .collect(Collectors.toList());

        List<ProductResponse> topRated =
                getTopRatedProducts();

        List<ProductResponse> topSelling =
                getTopSellingProducts();

        HomeProductsResponse response =
                HomeProductsResponse.builder()
                        .newlyReleased(newlyReleased)
                        .topSelling(topSelling)
                        .exclusiveDeals(exclusiveDeals)
                        .topRated(topRated)
                        .build();

        if (cacheProperties.isEnabled()) {
            productCacheService.cacheHomeProducts(
                    response,
                    cacheProperties.getHomeTtl()
            );
        }

        return response;
    }

    private List<ProductResponse> getTopRatedProducts() {

        List<ProductRatingSummary> ratingSummaries =
                reviewRepository.findTopRatedProducts(
                        PageRequest.of(0, SECTION_SIZE)
                );

        List<Long> productIds =
                ratingSummaries.stream()
                        .map(ProductRatingSummary::getProductId)
                        .collect(Collectors.toList());

        Map<Long, Product> productsById =
                productRepository.findAllById(productIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Product::getId,
                                Function.identity()
                        ));

        return ratingSummaries.stream()
                .filter(summary ->
                        productsById.containsKey(
                                summary.getProductId()
                        )
                )
                .map(summary -> {

                    Product product =
                            productsById.get(
                                    summary.getProductId()
                            );

                    ProductResponse response =
                            productMapper.toResponse(product);

                    response.setAverageRating(
                            summary.getAverageRating()
                    );

                    response.setTotalRatings(
                            Math.toIntExact(
                                    summary.getTotalRatings()
                            )
                    );

                    return response;
                })
                .collect(Collectors.toList());
    }

    private List<ProductResponse> getTopSellingProducts() {

        List<ProductSalesSummary> salesSummaries =
                orderItemRepository.findTopSellingProducts(
                        PageRequest.of(0, SECTION_SIZE)
                );

        List<Long> productIds =
                salesSummaries.stream()
                        .map(ProductSalesSummary::getProductId)
                        .collect(Collectors.toList());

        Map<Long, Product> productsById =
                productRepository.findAllById(productIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Product::getId,
                                Function.identity()
                        ));

        return salesSummaries.stream()
                .filter(summary ->
                        productsById.containsKey(
                                summary.getProductId()
                        )
                )
                .map(summary ->
                        productMapper.toResponse(
                                productsById.get(
                                        summary.getProductId()
                                )
                        )
                )
                .collect(Collectors.toList());
    }
}