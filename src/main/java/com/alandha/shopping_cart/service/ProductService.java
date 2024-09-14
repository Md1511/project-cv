package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public List<Product> getAllProducts();

    public Boolean deleteProduct(int id);

    public Product getProductById(int id);

    public List<Product> getAllActiveProducts(String category);

    public List<Product> searchProduct(String keyword);

    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);

    public Product findByTitle(String title);

    public Page<Product> getAllProductPagination2(Integer pageNo, Integer pageSize);

    Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize,  String ch);
}
