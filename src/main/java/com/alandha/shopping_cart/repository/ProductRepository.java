package com.alandha.shopping_cart.repository;

import com.alandha.shopping_cart.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByIsActiveTrue();

    List<Product> findByCategoryAndIsActiveTrue(String category);

    List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String title2);

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByCategory(Pageable pageable, String category);

    Page<Product> findByIsActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String title2, Pageable pageable);

    Product findByTitle(String title);


}
