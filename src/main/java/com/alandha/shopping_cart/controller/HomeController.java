package com.alandha.shopping_cart.controller;

import com.alandha.shopping_cart.groupValidator.Register;
import com.alandha.shopping_cart.service.CartService;
import com.alandha.shopping_cart.service.CategoryService;
import com.alandha.shopping_cart.service.ProductService;
import com.alandha.shopping_cart.service.UserService;
import com.alandha.shopping_cart.model.Category;
import com.alandha.shopping_cart.model.Product;
import com.alandha.shopping_cart.model.User;
import com.alandha.shopping_cart.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@ControllerAdvice(assignableTypes = {AdminController.class, UserController.class})
@SessionAttributes({"userr", "countCart", "categories"})
public class HomeController {

    @Autowired
    private CategoryService service;

    @Autowired
    private ProductService pservice;

    @Autowired
    private UserService uservice;

    @Autowired
    private CommonUtil commonutil;

    @Autowired
    private CartService cservice;

//    @Autowired
//    private CommonUtil commonUtil;
//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if(p != null) {
            String name = p.getName();
            User user = uservice.findUserByEmail(name);

            m.addAttribute("userr", user);

            Integer countCart = cservice.getCountCart(user.getId());
            m.addAttribute("countCart", countCart);
        }
//        List<Category> categories = service.getAllActiveCategory();
//        m.addAttribute("categoriess", categories);
    }

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("categories", service.getAllCategories().stream()
                .sorted((c1,c2)->c2.getId().compareTo(c1.getId()))
                .limit(6).toList());
        model.addAttribute("products", pservice.getAllProducts().stream()
                .sorted((p1,p2)->p2.getId().compareTo(p1.getId()))
                .limit(4).toList());


        return "index";
    }

    @GetMapping("/signin")
    public String login(Model model) {

        model.addAttribute("oke", new User());

        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam(value = "categoryy", defaultValue = "") String category,
                           @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize) {
//        System.out.println("Category: " + category);

        m.addAttribute("oke", false);


        List<Category> categories = service.getAllActiveCategory();
        m.addAttribute("categories", categories);
        m.addAttribute("paramValue", category);

        Page<Product> pages = pservice.getAllActiveProductPagination(pageNo, pageSize, category);


        List<Product> products = pages.getContent();

        m.addAttribute("productsSize", products.size());

        m.addAttribute("products", products);
        m.addAttribute("pageNo", pages.getNumber());
        m.addAttribute("pageSize", pages.getSize());
        m.addAttribute("totalElements", pages.getTotalElements());
        m.addAttribute("totalPages", pages.getTotalPages());
        m.addAttribute("isFirst", pages.isFirst());
        m.addAttribute("isLast", pages.isLast());


        return "product";
    }


    @GetMapping("/productskey")
    public String productskey(Model m,
                           @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name = "pageSize", defaultValue = "4") Integer pageSize,
                           @RequestParam(defaultValue = "") String keyword) {

        m.addAttribute("oke", true);

//        System.out.println("Category: " + category);

        List<Category> categories = service.getAllActiveCategory();
//        List<Product> products = pservice.getAllActiveProducts(category);

//        m.addAttribute("categories", categories);
//        m.addAttribute("products", products);
//        m.addAttribute("paramValue", category);

        Page<Product> pages = pservice.searchActiveProductPagination(pageNo, pageSize, keyword);

        List<Product> products = pages.getContent();

        m.addAttribute("productsSize", products.size());

        m.addAttribute("key", keyword);

        m.addAttribute("products", products);
        m.addAttribute("pageNo", pages.getNumber());
        m.addAttribute("pageSize", pages.getSize());
        m.addAttribute("totalElements", pages.getTotalElements());
        m.addAttribute("totalPages", pages.getTotalPages());
        m.addAttribute("isFirst", pages.isFirst());
        m.addAttribute("isLast", pages.isLast());


        return "product";
    }




    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model m) {
       Product productById = pservice.getProductById(id);
        m.addAttribute("product", productById);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@Validated(Register.class) @ModelAttribute User user, BindingResult result, @RequestParam("img") MultipartFile file, HttpSession session, Model model) throws IOException {

        if(result.hasErrors()) {
            result.getAllErrors().forEach(ok -> System.out.println(ok.getDefaultMessage()));
            return "register";
        }

        // Kiểm tra nếu file trống, gán ảnh mặc định
        String imageName = !file.isEmpty() ? file.getOriginalFilename() : "default.jpg";
        user.setProfileImage(imageName);

        User exisUser = uservice.findUserByEmail(user.getEmail());

        if(ObjectUtils.isEmpty(exisUser)) {
            User savedUser = uservice.saveUser(user);

            if(ObjectUtils.isEmpty(savedUser)) {
                session.setAttribute("errorMsg", "Not saved! Internal server error");
            } else {
                // Nếu người dùng có tải ảnh lên, tiến hành lưu ảnh
                if (!file.isEmpty()) {
                    // Đường dẫn tuyệt đối tới thư mục lưu trữ hình ảnh
                    String uploadDir = "src/main/resources/static/img/profile_img";
                    Path path = Paths.get(uploadDir, file.getOriginalFilename());

                    // Tạo thư mục nếu chưa tồn tại
                    Files.createDirectories(path.getParent());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }

                session.setAttribute("succMsg", "Registered Successfully");
            }
        } else {
            session.setAttribute("errorMsg", "This user is already existed");
        }

        return "redirect:/register";
    }


    // Forgot Password logic



    @PostMapping("/forgot-password")
    public String processP(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {


        User user = uservice.findUserByEmail(email);

        if(ObjectUtils.isEmpty(user)) {
            session.setAttribute("errorMsg", "Invalid email");
        } else {
            String resetToken = UUID.randomUUID().toString();
            uservice.updateUserResetToken(email, resetToken);

            // Generate URI : http://localhost:/reset_password?token=gqjglqjgleq

            String url = CommonUtil.generateUrl(request)+"/reset-password?token="+resetToken;

            Boolean sendMail = commonutil.sendMail(url, email);

            if(sendMail) {
                session.setAttribute("succMsg", "Please check your email. Password Reset link sent");
            } else {
                session.setAttribute("errorMsg", "Something went wrong on server ! Email not send");
            }
        }

        return "redirect:/forgot-password";
    }

    @GetMapping("/forgot-password")
    public String showForgotP() {
        return "forgot_password";
    }


    @GetMapping("/reset-password")
    public String showResetP(@RequestParam String token, Model model) {

        User userByToken = uservice.findUserByToken(token);

        if(ObjectUtils.isEmpty(userByToken)) {
            model.addAttribute("msg", "Your link is invalid or expired");
            return "message";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetP(@RequestParam String token, Model model, @RequestParam String password, HttpSession session) {

        User userByToken = uservice.findUserByToken(token);

        if(ObjectUtils.isEmpty(userByToken)) {
            model.addAttribute("msg", "Your link is invalid or expired");
            return "message";
        } else {
            System.out.println(password);
//                userByToken.setPassword(passwordEncoder.encode(password));
                userByToken.setPassword(password);
                userByToken.setResetToken(null);
                uservice.saveForgot(userByToken);
                model.addAttribute("msg", "Password change successfully");
            return "message";
        }
    }




//    @PostMapping("/search")
//    public String searchProduct(@RequestParam String keyword, Model model) {
//
//        List<Product> products = pservice.searchProduct(keyword);
//
//        model.addAttribute("products", products);
//
//        return "product";
//    }
}
