package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    public User saveUser(User user);

    public User saveForgot(User user);

    public User saveAdmin(User user);

    public User findUserByEmail(String email);

//    public User findUserByUsername(String username);

    public List<User> findUserByRole(String role);

    Boolean updateAccountStatus(Integer id, Boolean status);

    public void increaseFiledAttempt(User user);

    public void userAccountLock(User user);

    public boolean unlockAccountTimeExpired(User user);

    public void resetAttempt(int userId);

    void updateUserResetToken(String email, String resetToken);

    public User findUserByToken(String token);

    public User updateUser(User user);

    public User updateUserProfile(User user, MultipartFile profilePicture);


}
