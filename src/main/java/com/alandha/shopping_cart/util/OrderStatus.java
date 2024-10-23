package com.alandha.shopping_cart.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum OrderStatus {

    IN_PROGRESS(1, "In Progress"),
    ORDER_RECEIVED(2,"Order Received"),
    PRODUCT_PACKED(3, "Product Packed"),
    OUT_FOR_DELIVERY(4, "Our for Delivery"),
    DELIVERED(5, "Delivered"),
    CANCEL(6, "Cancelled"),
    SUCCESS(7, "Order Success");

    private Integer id;

    private String name;


}
