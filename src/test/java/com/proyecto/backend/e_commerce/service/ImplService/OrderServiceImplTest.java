package com.proyecto.backend.e_commerce.service.ImplService;


import com.proyecto.backend.e_commerce.domain.Inventory;
import com.proyecto.backend.e_commerce.domain.Order;
import com.proyecto.backend.e_commerce.domain.Product;
import com.proyecto.backend.e_commerce.domain.User;
import com.proyecto.backend.e_commerce.dto.CreateOrderRequestDto;
import com.proyecto.backend.e_commerce.dto.OrderDto;
import com.proyecto.backend.e_commerce.dto.OrderItemRequestDto;
import com.proyecto.backend.e_commerce.repository.InventoryRepository;
import com.proyecto.backend.e_commerce.repository.OrderRepository;
import com.proyecto.backend.e_commerce.repository.ProductRepository;
import com.proyecto.backend.e_commerce.repository.UserRepository;
import com.proyecto.backend.e_commerce.service.Iservice.IOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.mockito.Mockito.verify;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Inventory inventory;
    private CreateOrderRequestDto orderRequest;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFrequent(false);

        product = new Product();
        product.setId(1L);
        product.setName("Producto Test");
        product.setPrice(new BigDecimal("10.00"));

        inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(10);

        OrderItemRequestDto orderItemRequest = new OrderItemRequestDto();
        orderItemRequest.setProductId(1L);
        orderItemRequest.setQuantity(2);

        orderRequest = new CreateOrderRequestDto();
        orderRequest.setItems(Collections.singletonList(orderItemRequest));


        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("testuser");
        SecurityContextHolder.setContext(securityContext);
    }

    @DisplayName("Prueba para crear una orden exitosamente")
    @Test
    void whenCreateOrder_withSufficientStock_thenOrderIsCreatedAndStockIsReduced() {

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(inventoryRepository.findByProductId(1L)).willReturn(Optional.of(inventory));
        given(orderRepository.save(any())).willAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(1L); // Simulamos que la BD le asigna un ID
            return orderToSave;
        });


        OrderDto createdOrder = orderService.createOrder(orderRequest);

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getTotalAmount()).isEqualByComparingTo("20.00");
        assertThat(createdOrder.getItems().get(0).getQuantity()).isEqualTo(2);


        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(inventoryCaptor.capture());
        assertThat(inventoryCaptor.getValue().getQuantity()).isEqualTo(8);
    }

    @DisplayName("Prueba para fallar creación de orden por stock insuficiente")
    @Test
    void whenCreateOrder_withInsufficientStock_thenThrowException() {

        inventory.setQuantity(1);
        orderRequest.getItems().get(0).setQuantity(2);

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(inventoryRepository.findByProductId(1L)).willReturn(Optional.of(inventory));


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("Stock insuficiente para el producto: Producto Test");
    }
}
