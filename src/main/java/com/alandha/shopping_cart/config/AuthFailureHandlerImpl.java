package com.alandha.shopping_cart.config;

import com.alandha.shopping_cart.model.User;
import com.alandha.shopping_cart.repository.UserRepository;
import com.alandha.shopping_cart.service.UserService;
import com.alandha.shopping_cart.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository repo;

    @Autowired
    private UserService service;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {


        String email = request.getParameter("username");
        User user = repo.findUserByEmail(email);


        if(user!=null) {

            if (user.getIsEnable()) {

                if (user.getAccountNonLocked()) {
                    if (user.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
                        service.increaseFiledAttempt(user);
                    } else {
                        service.userAccountLock(user);
                        exception = new LockedException("Your account is locked !! failed attempt 3");
                    }

                } else {
                    if (service.unlockAccountTimeExpired(user)) {
                        exception = new LockedException("Your account is unlocked !! Please try to login");
                    } else {
                        exception = new LockedException("Your account is Locked !! Please try after sometimes");
                    }
                }

            } else {
                exception = new LockedException("Your account is inactive");
            }
        } else {
            exception = new LockedException("Email & Password is invalid");
        }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
