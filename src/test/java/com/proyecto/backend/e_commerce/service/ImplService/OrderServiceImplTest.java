package com.proyecto.backend.e_commerce.service.ImplService;

import com.proyecto.backend.e_commerce.domain.*;
import com.proyecto.backend.e_commerce.domain.Order;
import com.proyecto.backend.e_commerce.dto.CreateOrderRequestDto;
import com.proyecto.backend.e_commerce.dto.OrderDto;
import com.proyecto.backend.e_commerce.dto.OrderItemRequestDto;
import com.proyecto.backend.e_commerce.repository.InventoryRepository;
import com.proyecto.backend.e_commerce.repository.OrderRepository;
import com.proyecto.backend.e_commerce.repository.ProductRepository;
import com.proyecto.backend.e_commerce.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Inventory inventory;
    private CreateOrderRequestDto orderRequest;

    // --- helper: mockear SecurityContext SOLO cuando se use ---
    private void mockAuthUser(String username) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(username);
        SecurityContextHolder.setContext(securityContext);
    }

    @BeforeEach
    void setUp() {
        // usuario base
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFrequent(false);

        // producto base
        product = new Product();
        product.setId(1L);
        product.setName("Producto Test");
        product.setPrice(new BigDecimal("10.00"));

        // inventario base
        inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(10);

        // request base
        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductId(1L);
        item.setQuantity(2);

        orderRequest = new CreateOrderRequestDto();
        orderRequest.setItems(Collections.singletonList(item));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @DisplayName("Crea orden con stock suficiente y descuenta inventario")
    @Test
    void whenCreateOrder_withSufficientStock_thenOrderIsCreatedAndStockIsReduced() {
        mockAuthUser("testuser");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(inventoryRepository.findByProduct_Id(1L)).willReturn(Optional.of(inventory));
        given(orderRepository.save(any())).willAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(1L);
            return orderToSave;
        });

        OrderDto createdOrder = orderService.createOrder(orderRequest);

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getTotalAmount()).isEqualByComparingTo("20.00");
        assertThat(createdOrder.getItems().get(0).getQuantity()).isEqualTo(2);

        ArgumentCaptor<Inventory> invCap = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(invCap.capture());
        assertThat(invCap.getValue().getQuantity()).isEqualTo(8);
    }

    @DisplayName("Falla por stock insuficiente")
    @Test
    void whenCreateOrder_withInsufficientStock_thenThrowException() {
        mockAuthUser("testuser");

        // dejar 1 en inventario y pedir 2
        inventory.setQuantity(1);
        orderRequest.getItems().get(0).setQuantity(2);

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(inventoryRepository.findByProduct_Id(1L)).willReturn(Optional.of(inventory));

        var ex = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderRequest));
        assertThat(ex.getMessage()).isEqualTo("Stock insuficiente para el producto: Producto Test");
    }

    @DisplayName("getOrderById devuelve DTO mapeado correctamente")
    @Test
    void getOrderById_returnsMappedDto() {
        // NO se usa SecurityContext en getOrderById → no mockeamos nada de auth
        user.setFirstName("Luis");
        user.setLastName("Avila");

        Order o = new Order();
        o.setId(99L);
        o.setUser(user);
        o.setStatus("COMPLETED");
        o.setTotalAmount(new BigDecimal("30.00"));
        o.setDiscountApplied(BigDecimal.ZERO);
        o.setFinalAmount(new BigDecimal("30.00"));

        OrderItem it = new OrderItem();
        it.setOrder(o);
        it.setProduct(product);
        it.setQuantity(3);
        it.setPricePerUnit(product.getPrice());
        o.setItems(List.of(it));

        given(orderRepository.findById(99L)).willReturn(Optional.of(o));

        OrderDto dto = orderService.getOrderById(99L);

        assertThat(dto.getId()).isEqualTo(99L);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getItems().size()).isEqualTo(1);
        assertThat(dto.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @DisplayName("getOrdersForCurrentUser mapea lista de órdenes")
    @Test
    void getOrdersForCurrentUser_mapsList() {
        mockAuthUser("testuser");

        Order o = new Order();
        o.setId(5L);
        o.setUser(user);
        o.setStatus("COMPLETED");
        o.setTotalAmount(new BigDecimal("10.00"));
        o.setDiscountApplied(BigDecimal.ZERO);
        o.setFinalAmount(new BigDecimal("10.00"));
        o.setItems(List.of());

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(orderRepository.findByUserId(1L)).willReturn(List.of(o));

        var list = orderService.getOrdersForCurrentUser();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getId()).isEqualTo(5L);
        assertThat(list.get(0).getUsername()).isEqualTo("testuser");
    }

    @DisplayName("Aplica 50% cuando randomOrder=true y dentro de la ventana de promoción")
    @Test
    void createOrder_randomOrder_applies50Percent() {
        mockAuthUser("testuser");

        ReflectionTestUtils.setField(orderService, "promotionEnabled", true);
        ReflectionTestUtils.setField(orderService, "promotionStartDate", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(orderService, "promotionEndDate", LocalDateTime.now().plusDays(1));

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(inventoryRepository.findByProduct_Id(1L)).willReturn(Optional.of(inventory));
        given(orderRepository.save(any())).willAnswer(inv -> {
            Order ord = inv.getArgument(0);
            ord.setId(123L);
            return ord;
        });

        orderRequest.setRandomOrder(true);

        OrderDto dto = orderService.createOrder(orderRequest);
        assertThat(dto.getTotalAmount()).isEqualByComparingTo("20.00");
        assertThat(dto.getDiscountApplied()).isEqualByComparingTo("10.00");
        assertThat(dto.getFinalAmount()).isEqualByComparingTo("10.00");
    }

    @DisplayName("Aplica 15% (10% promo + 5% frecuente) y redondea")
    @Test
    void createOrder_promoAndFrequent_applies15Percent() {
        mockAuthUser("testuser");

        ReflectionTestUtils.setField(orderService, "promotionEnabled", true);
        ReflectionTestUtils.setField(orderService, "promotionStartDate", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(orderService, "promotionEndDate", LocalDateTime.now().plusDays(1));

        user.setFrequent(true);

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(inventoryRepository.findByProduct_Id(1L)).willReturn(Optional.of(inventory));
        given(orderRepository.save(any())).willAnswer(inv -> {
            Order ord = inv.getArgument(0);
            ord.setId(124L);
            return ord;
        });

        orderRequest.setRandomOrder(false);

        OrderDto dto = orderService.createOrder(orderRequest);
        assertThat(dto.getTotalAmount()).isEqualByComparingTo("20.00");
        assertThat(dto.getDiscountApplied()).isEqualByComparingTo("3.00");
        assertThat(dto.getFinalAmount()).isEqualByComparingTo("17.00");
    }
}
