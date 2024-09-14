package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.OrderRequest;
import com.alandha.shopping_cart.model.Product;
import com.alandha.shopping_cart.model.ProductOrder;
import jakarta.mail.MessagingException;
import org.hibernate.query.Order;
import org.springframework.data.domain.Page;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException;

    public List<ProductOrder> getOrdersByUser(Integer userId);

    public ProductOrder updateOrderStatus(Integer id, String status);

    public List<ProductOrder> getAllOrders();

    public ProductOrder getOrdersByOrderId(String orderId);

    public Page<ProductOrder> getAllOrderIdPagination(Integer pageNo, Integer pageSize);

}
