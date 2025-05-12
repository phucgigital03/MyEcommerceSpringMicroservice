package com.example.ecom_application.service;

import com.example.ecom_application.dto.ProductRequest;
import com.example.ecom_application.dto.ProductResponse;
import com.example.ecom_application.model.Product;
import com.example.ecom_application.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product;
        product = modelMapper.map(productRequest, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponse.class);
    }


    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id).map(existingProduct ->{
            existingProduct.setName(productRequest.getName());
            existingProduct.setDescription(productRequest.getDescription());
            existingProduct.setPrice(productRequest.getPrice());
            existingProduct.setStockQuantity(productRequest.getStockQuantity());
            existingProduct.setCategory(productRequest.getCategory());
            existingProduct.setImageUrl(productRequest.getImageUrl());
            Product updatedProduct = productRepository.save(existingProduct);
            return modelMapper.map(updatedProduct, ProductResponse.class);
        });
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findByActiveTrue();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .toList();
    }


    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setActive(false);
            productRepository.save(existingProduct);
            return true;
        }).orElse(false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }
}
