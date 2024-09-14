package com.alandha.shopping_cart.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 500)
    @NotBlank(message = "Your title can't be blank")
    @Size(min = 5, max = 55, message = "Your title should have between 5 and 55")
    private String title;

    @Column(length = 5000)
    @NotBlank(message = "Your Description can't be blank")
    @Size(min = 5, max = 155, message = "Your title should have between 5 and 155")
    private String description;

//    @NotNull(message = "Please select the product type")
    private String category;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Stock cannot be null")
    @Positive(message = "Stock must be positive")
    private int stock;

    private String image;

    private int discount;

    private Double discountPrice;

    private Boolean isActive;

}
