package com.alandha.shopping_cart;

import com.alandha.shopping_cart.model.Category;
import com.alandha.shopping_cart.repository.CategoryRepository;
import com.alandha.shopping_cart.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;

@SpringBootApplication
public class ShoppingCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApplication.class, args);
    }

    }

