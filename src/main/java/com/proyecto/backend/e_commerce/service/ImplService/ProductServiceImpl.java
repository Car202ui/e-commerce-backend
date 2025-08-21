package com.proyecto.backend.e_commerce.service.ImplService;

import com.proyecto.backend.e_commerce.dto.ProductDto;
import com.proyecto.backend.e_commerce.domain.Product;
import com.proyecto.backend.e_commerce.exception.ResourceNotFoundException;
import com.proyecto.backend.e_commerce.repository.ProductRepository;
import com.proyecto.backend.e_commerce.service.Iservice.IProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = mapToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    @Override
    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productId));
        return mapToDto(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productId));

        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setActive(productDto.isActive());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + productId);
        }
        productRepository.deleteById(productId);
    }
    
    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setActive(product.isActive());
        return dto;
    }

    private Product mapToEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setActive(dto.isActive());
        return product;
    }

    @Override
    public List<ProductDto> getActiveProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
