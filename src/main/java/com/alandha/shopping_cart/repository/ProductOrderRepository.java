package com.alandha.shopping_cart.repository;

import com.alandha.shopping_cart.model.Product;
import com.alandha.shopping_cart.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    List<ProductOrder> findByUserId(Integer userId);

    ProductOrder findOrderIdByOrderId(String orderId);



}
