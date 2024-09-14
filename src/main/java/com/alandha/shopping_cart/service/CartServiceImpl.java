package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.Cart;
import com.alandha.shopping_cart.model.Product;
import com.alandha.shopping_cart.model.User;
import com.alandha.shopping_cart.repository.CartRepository;
import com.alandha.shopping_cart.repository.ProductRepository;
import com.alandha.shopping_cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;


    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        User user = userRepo.findById(userId).get();
        Product product = productRepo.findById(productId).get();

        Cart cartStus = cartRepository.findByUserIdAndProductId(userId, productId);

        Cart cart = null;

        if(ObjectUtils.isEmpty(cartStus)) {
            cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getPrice());
        } else {
            cart = cartStus;
            cart.setQuantity(cartStus.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getCartByUserId(Integer userId) {
       List<Cart> cartList = cartRepository.findByUserId(userId);

       Double totalOrderPrice = 0.0;

       List<Cart> updateCarts = new ArrayList<>();
       for(Cart cart : cartList) {
            Double totalPrice = (cart.getProduct().getDiscountPrice() * cart.getQuantity());

            cart.setTotalPrice(totalPrice);
            totalOrderPrice += totalPrice;
            cart.setTotalOrderPrice(totalOrderPrice);
            updateCarts.add(cart);
       }
       return updateCarts;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer countByUserId = cartRepository.countByUserId(userId);
        return countByUserId;
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart = cartRepository.findById(cid).get();
        int updateQuantity;

        if(sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() - 1;

            if(updateQuantity <= 0) {
                cartRepository.deleteById(cart.getId());
            } else {
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }
        } else {
            updateQuantity = cart.getQuantity()+1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }


    }
}
