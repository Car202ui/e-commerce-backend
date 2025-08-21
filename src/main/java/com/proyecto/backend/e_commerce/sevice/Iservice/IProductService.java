package com.proyecto.backend.e_commerce.sevice.Iservice;

import com.proyecto.backend.e_commerce.Dtos.ProductDto;

import java.util.List;

public interface IProductService {

    ProductDto createProduct(ProductDto productDto);
    ProductDto getProductById(Long productId);
    List<ProductDto> getAllProducts();
    ProductDto updateProduct(Long productId, ProductDto productDto);
    void deleteProduct(Long productId);
}
