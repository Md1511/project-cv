package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.Cart;
import org.springframework.ui.Model;

import java.util.List;

public interface CartService {
    public Cart saveCart(Integer productId, Integer userId);

    public List<Cart> getCartByUserId(Integer userId);

    public Integer getCountCart(Integer userId);

    public void  updateQuantity(String sy, Integer cid);
}
