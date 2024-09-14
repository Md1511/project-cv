package com.alandha.shopping_cart.service;

import com.alandha.shopping_cart.model.User;
import com.alandha.shopping_cart.repository.UserRepository;
import com.alandha.shopping_cart.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        user.setRole("ROLE_USER");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        return repo.save(user);
    }

    @Override
    public User saveAdmin(User user) {
        user.setRole("ROLE_ADMIN");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        return repo.save(user);
    }

    @Override
    public User saveForgot(User user) {
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        return repo.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return repo.findUserByEmail(email);
    }

    @Override
    public List<User> findUserByRole(String role) {
        return repo.findUserByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {

        Optional<User> findByUser = repo.findById(id);

        if(findByUser.isPresent()) {
            User user = findByUser.get();
            user.setIsEnable(status);
            repo.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFiledAttempt(User user) {
        int attempt = user.getFailedAttempt()+1;
        user.setFailedAttempt(attempt);
        repo.save(user);
    }

    @Override
    public void userAccountLock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        repo.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(User user) {

        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime+ AppConstant.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if(unlockTime < currentTime) {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            repo.save(user);
            return true;
        }

        return false;
    }

    @Override
    public void resetAttempt(int userId) {

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        User findByEmail = repo.findUserByEmail(email);
        findByEmail.setResetToken(resetToken);
        repo.save(findByEmail);
    }

    @Override
    public User findUserByToken(String token) {
        return repo.findByResetToken(token);
    }

    @Override
    public User updateUser(User user) {
        return repo.save(user);
    }

    @Override
    public User updateUserProfile(User user, MultipartFile img) {

        User userr = repo.findById(user.getId()).get();

//        String imageFile = !img.isEmpty() ? img.getOriginalFilename() : "default.jpg";

//        if(!img.isEmpty()) {
//
//        }



        if(!ObjectUtils.isEmpty(userr) && !img.isEmpty()) {

            userr.setProfileImage(img.getOriginalFilename());
            userr.setName(user.getName());
            userr.setMobileNumber(user.getMobileNumber());
            userr.setAddress(user.getAddress());
            userr.setCity(user.getCity());
            userr.setState(user.getState());
            userr.setPincode(user.getPincode());

            repo.save(userr);


            String uploadDir = "src/main/resources/static/img/profile_img";
            Path path = Paths.get(uploadDir, img.getOriginalFilename());

            // Tạo thư mục nếu chưa tồn tại
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            userr.setName(user.getName());
            userr.setMobileNumber(user.getMobileNumber());
            userr.setAddress(user.getAddress());
            userr.setCity(user.getCity());
            userr.setState(user.getState());
            userr.setPincode(user.getPincode());

            repo.save(userr);
        }
        return userr;
    }
}
