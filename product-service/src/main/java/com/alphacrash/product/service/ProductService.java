package com.alphacrash.product.service;

import com.alphacrash.product.dto.ProductRequest;
import com.alphacrash.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    public void createProduct(ProductRequest productRequest);
    public List<ProductResponse> getAllProducts();
}
