package com.app.ecommerce.service;

import com.app.ecommerce.entity.Order;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.modules.product.api.HomeCatalogApi;
import com.app.ecommerce.modules.product.api.ProductApi;
import com.app.ecommerce.modules.product.api.ProductInfo;
import com.app.ecommerce.repository.*;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductApi productApi;

    @Mock
    private HomeCatalogApi homeCatalogApi;

    @Mock
    private Session session;

    @InjectMocks
    private OrderService orderService;


    @Test
    void shouldSaveOrderAndEvictHomeCache_whenStripeOrderIsCompleted() {

        Long userId = 1L;
        Long productId = 10L;

        User user = new User();
        user.setId(userId);

        ProductInfo productInfo =
                ProductInfo.builder()
                        .id(productId)
                        .name("iPhone 16")
                        .price(BigDecimal.valueOf(50))
                        .stock(20)
                        .build();

        Map<String, String> metadata =
                createMetadata(userId, productId);

        when(session.getId())
                .thenReturn("cs_test_123");

        when(session.getMetadata())
                .thenReturn(metadata);

        when(session.getAmountTotal())
                .thenReturn(11800L);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(productApi.getProductInfo(productId))
                .thenReturn(productInfo);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {

                    Order order =
                            invocation.getArgument(0);

                    order.setId(100L);

                    return order;
                });

        orderService.saveOrder(session);

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository)
                .save(orderCaptor.capture());

        Order savedOrder =
                orderCaptor.getValue();

        assertEquals(user, savedOrder.getUser());

        assertEquals(
                "Jagjeet",
                savedOrder.getFirstName()
        );

        assertEquals(
                "Singh",
                savedOrder.getLastName()
        );

        assertNull(savedOrder.getEmail());

        assertEquals(
                100.0,
                savedOrder.getSubtotal()
        );

        assertEquals(
                13.0,
                savedOrder.getTax()
        );

        assertEquals(
                5.0,
                savedOrder.getShippingCharge()
        );

        assertEquals(
                118.0,
                savedOrder.getTotal()
        );

        assertNotNull(savedOrder.getOrderItems());

        assertEquals(
                1,
                savedOrder.getOrderItems().size()
        );

        assertEquals(
                productId,
                savedOrder.getOrderItems()
                        .get(0)
                        .getProductId()
        );

        assertEquals(
                2,
                savedOrder.getOrderItems()
                        .get(0)
                        .getQuantity()
        );

        assertEquals(
                50.0,
                savedOrder.getOrderItems()
                        .get(0)
                        .getPrice()
        );

        verify(productApi)
                .getProductInfo(productId);

        verify(homeCatalogApi)
                .evictHomeCache();
    }


    @Test
    void shouldNotEvictHomeCache_whenOrderSaveFails() {

        Long userId = 1L;
        Long productId = 10L;

        User user = new User();
        user.setId(userId);

        ProductInfo productInfo =
                ProductInfo.builder()
                        .id(productId)
                        .name("iPhone 16")
                        .price(BigDecimal.valueOf(50))
                        .stock(20)
                        .build();

        Map<String, String> metadata =
                createMetadata(userId, productId);

        when(session.getId())
                .thenReturn("cs_test_123");

        when(session.getMetadata())
                .thenReturn(metadata);

        when(session.getAmountTotal())
                .thenReturn(11800L);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(productApi.getProductInfo(productId))
                .thenReturn(productInfo);

        when(orderRepository.save(any(Order.class)))
                .thenThrow(
                        new RuntimeException("Database failure")
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> orderService.saveOrder(session)
                );

        assertEquals(
                "Database failure",
                exception.getMessage()
        );

        verify(orderRepository)
                .save(any(Order.class));

        verify(homeCatalogApi, never())
                .evictHomeCache();
    }


    private Map<String, String> createMetadata(
            Long userId,
            Long productId
    ) {

        Map<String, String> metadata =
                new HashMap<>();

        metadata.put(
                "userId",
                String.valueOf(userId)
        );

        metadata.put(
                "firstName",
                "Jagjeet"
        );

        metadata.put(
                "lastName",
                "Singh"
        );

        metadata.put(
                "phone",
                "1234567890"
        );

        metadata.put(
                "address",
                "123 Main Street"
        );

        metadata.put(
                "city",
                "Toronto"
        );

        metadata.put(
                "postalCode",
                "A1A1A1"
        );

        metadata.put(
                "state",
                "Ontario"
        );

        metadata.put(
                "subtotal",
                "100.0"
        );

        metadata.put(
                "tax",
                "13.0"
        );

        metadata.put(
                "shipping",
                "5.0"
        );

        metadata.put(
                "products",
                """
                [
                  {
                    "productId": %d,
                    "quantity": 2,
                    "productPrice": 50.0
                  }
                ]
                """.formatted(productId)
        );

        return metadata;
    }
}