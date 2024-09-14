package com.alandha.shopping_cart.config;


import com.alandha.shopping_cart.model.User;
import com.alandha.shopping_cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String usernameee) throws UsernameNotFoundException {

        User user = repo.findUserByEmail(usernameee);

        if(ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUser(user);
    }
}
