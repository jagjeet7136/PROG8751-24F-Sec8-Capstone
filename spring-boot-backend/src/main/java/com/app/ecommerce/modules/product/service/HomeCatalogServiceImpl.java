package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.modules.product.domain.mapper.ProductMapper;
import com.app.ecommerce.modules.product.dto.response.HomeProductsResponse;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.app.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeCatalogServiceImpl implements HomeCatalogService {

    private static final int SECTION_SIZE = 8;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public HomeProductsResponse getHomeProducts() {

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

        return HomeProductsResponse.builder()
                .newlyReleased(newlyReleased)
                .exclusiveDeals(exclusiveDeals)

                .topSelling(Collections.emptyList())
                .topRated(Collections.emptyList())

                .build();
    }
}