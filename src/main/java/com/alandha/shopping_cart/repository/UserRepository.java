package com.alandha.shopping_cart.repository;

import com.alandha.shopping_cart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.relation.Role;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    public User findUserByEmail(String email);

//    public User findUserByName(String username);

    public List<User> findUserByRole(String role);

    public User findByResetToken(String resetToken);
}
