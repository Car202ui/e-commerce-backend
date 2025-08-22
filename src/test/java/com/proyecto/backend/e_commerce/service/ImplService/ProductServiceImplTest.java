package com.proyecto.backend.e_commerce.service.ImplService;

import com.proyecto.backend.e_commerce.domain.Product;
import com.proyecto.backend.e_commerce.dto.ProductDto;
import com.proyecto.backend.e_commerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Producto de Prueba");
        product.setDescription("Descripción de prueba");
        product.setPrice(new BigDecimal("100.00"));
        product.setActive(true);
    }

    @DisplayName("Prueba para guardar un nuevo producto")
    @Test
    void whenCreateProduct_thenReturnProductObject() {

        given(productRepository.save(any(Product.class))).willReturn(product);

        ProductDto productDto = new ProductDto();
        productDto.setName("Producto de Prueba");

        ProductDto savedProduct = productService.createProduct(productDto);


        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Producto de Prueba");
    }

    @DisplayName("Prueba para obtener un producto por su ID")
    @Test
    void whenGetProductById_thenReturnProductObject() {

        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ProductDto foundProduct = productService.getProductById(1L);

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(1L);
    }
}
