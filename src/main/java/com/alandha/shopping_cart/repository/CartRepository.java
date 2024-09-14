package com.alandha.shopping_cart.repository;

import com.alandha.shopping_cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    public Cart findByUserIdAndProductId(Integer userId, Integer productId);

    public Integer countByUserId(Integer userId);

    public List<Cart> findByUserId(Integer userId);
}
