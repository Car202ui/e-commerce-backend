package com.proyecto.backend.e_commerce.service.Iservice;

import com.proyecto.backend.e_commerce.dto.ProductDto;

import java.util.List;

public interface IProductService {

    ProductDto createProduct(ProductDto productDto);
    ProductDto getProductById(Long productId);
    List<ProductDto> getAllProducts();
    ProductDto updateProduct(Long productId, ProductDto productDto);
    void deleteProduct(Long productId);
    List<ProductDto> getActiveProducts();
    List<ProductDto> searchProductsByName(String name);
}
