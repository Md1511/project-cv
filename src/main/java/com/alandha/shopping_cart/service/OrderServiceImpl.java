package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.*;
import com.alandha.shopping_cart.repository.CartRepository;
import com.alandha.shopping_cart.repository.ProductOrderRepository;
import com.alandha.shopping_cart.repository.ProductRepository;
import com.alandha.shopping_cart.util.CommonUtil;
import com.alandha.shopping_cart.util.OrderStatus;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException {
        List<Cart> carts = cartRepository.findByUserId(userId);

        for(Cart cart : carts) {
            Product product = cart.getProduct();
            product.setSold(product.getSold() + cart.getQuantity());
            product.setStock(product.getStock() - cart.getQuantity());

            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());

            order.setOrderDate(LocalDate.now());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());
            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());
            order.setStatus(OrderStatus.IN_PROGRESS.getName()   );
            order.setPaymentType(orderRequest.getPaymentType());


            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setMobileNo(orderRequest.getMobileNo());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());


            order.setOrderAddress(address);

            cartRepository.delete(cart);
            productRepository.save(product);
            ProductOrder saveOrder = productOrderRepository.save(order);
            productRepository.save(product);
            commonUtil.sendMailForProductOrder(saveOrder, "success");

        }
    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        List<ProductOrder> orders = productOrderRepository.findByUserId(userId);

        return orders;
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) {
        Optional<ProductOrder> findById = productOrderRepository.findById(id);

        if(findById.isPresent()) {
           ProductOrder productOrder = findById.get();
           productOrder.setStatus(status);
           ProductOrder updateOrder = productOrderRepository.save(productOrder);

           Product product = findById.get().getProduct();
           if(!ObjectUtils.isEmpty(product)) {
               product.setSold(product.getSold() - productOrder.getQuantity());
               product.setStock(product.getStock() + productOrder.getQuantity());
           }

           productRepository.save(product);
           return updateOrder;
        }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        return productOrderRepository.findAll();
    }

    @Override
    public ProductOrder getOrdersByOrderId(String orderId) {
        return productOrderRepository.findOrderIdByOrderId(orderId);
    }

    @Override
    public Page<ProductOrder> getAllOrderIdPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productOrderRepository.findAll(pageable);
    }
}
