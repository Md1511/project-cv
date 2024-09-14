package com.alandha.shopping_cart.controller;

import com.alandha.shopping_cart.groupValidator.Register;
import com.alandha.shopping_cart.model.ProductOrder;
import com.alandha.shopping_cart.model.User;
import com.alandha.shopping_cart.service.*;
import com.alandha.shopping_cart.model.Category;
import com.alandha.shopping_cart.model.Product;
import com.alandha.shopping_cart.util.CommonUtil;
import com.alandha.shopping_cart.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.el.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
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

@Controller
@RequestMapping("/admin")
@SessionAttributes({"orders", "categories"})
public class AdminController {

    @Autowired
    private CategoryService service;

    @Autowired
    private ProductService pservice;

    @Autowired
    private UserService uservice;

    @Autowired
    private OrderService oservice;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;




    @GetMapping("/")
    public String index() {
        return "admin/index";
    }
    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m) {
        List<Category> categories = service.getAllCategories();

        m.addAttribute("productt", new Product());

        m.addAttribute("categories", categories);
        return "admin/add_product";
    }
    @GetMapping("/category")
    public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                    @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize) {

//        m.addAttribute("categorys", service.getAllCategories());
        Page<Category> pages = service.getAllCategoryPagination(pageNo, pageSize);

        List<Category> categories = pages.getContent();

        m.addAttribute("categorySize", categories.size());

        m.addAttribute("categorys", categories);
        m.addAttribute("pageNo", pages.getNumber());
        m.addAttribute("pageSize", pages.getSize());
        m.addAttribute("totalElements", pages.getTotalElements());
        m.addAttribute("totalPages", pages.getTotalPages());
        m.addAttribute("isFirst", pages.isFirst());
        m.addAttribute("isLast", pages.isLast());

        m.addAttribute("category", new Category());

        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@Valid @ModelAttribute Category category,BindingResult result, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        if(result.hasErrors()) {
            return "admin/category";
        }


        String imageName = !file.isEmpty() ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean exisCategory = service.existCategory(category.getName());


        if (exisCategory) {
            session.setAttribute("errorMsg", "Category Name already exists");
        } else {
            Category savedCategory = service.saveCategory(category);

            if (ObjectUtils.isEmpty(savedCategory)) {
                session.setAttribute("errorMsg", "Not saved! Internal server error");
            } else {

                if(!file.isEmpty()) {
                    // Đường dẫn tuyệt đối tới thư mục lưu trữ hình ảnh
                    String uploadDir = "src/main/resources/static/img/category_img";
                    Path path = Paths.get(uploadDir, file.getOriginalFilename());

                    // Tạo thư mục nếu chưa tồn tại
                    Files.createDirectories(path.getParent());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }

                session.setAttribute("succMsg", "Saved Successfully");
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {
        Boolean deleted = service.deleteCateogry(id);
        if(deleted) {
            session.setAttribute("succMsg", "Deleted Successfully");
        } else {
            session.setAttribute("errorMsg", "Not Deleted! Internal server error");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model) {
        model.addAttribute("category", service.getCategoryById(id));
        return "admin/edit_category";
    }
    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        Category oldCategory = service.getCategoryById(category.getId());
        String fileName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

        if(!ObjectUtils.isEmpty(category)) {


            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(fileName);

//
        }

        Category newCategory = service.saveCategory(oldCategory);

        if(!ObjectUtils.isEmpty(newCategory) && !file.isEmpty()) {

            String uploadDir = "src/main/resources/static/img/category_img";
            Path path = Paths.get(uploadDir, file.getOriginalFilename());

            // Create folder if it not exist
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);


            session.setAttribute("succMsg", "Updated Successfully");

        } else if(!ObjectUtils.isEmpty(newCategory) && file.isEmpty()) {
            session.setAttribute("succMsg", "Updated Successfully");
        } else {
            session.setAttribute("errorMsg", "Not Updated! Internal server error");
        }


        return "redirect:/admin/loadEditCategory/" + category.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@Valid @ModelAttribute("productt") Product product, BindingResult result, @RequestParam("file") MultipartFile image, HttpSession session) throws IOException {
        if(result.hasErrors()) {
            return "admin/add_product";
        }

        // Kiểm tra nếu file trống, gán ảnh mặc định
        String imageName = !image.isEmpty() ? image.getOriginalFilename() : "default.jpg";

        Product productExisted = pservice.findByTitle(product.getTitle());

        System.out.println("Category's product:" + product.getCategory()+".");

        if(ObjectUtils.isEmpty(product.getCategory()) || product.getCategory() == "" || product.getCategory().trim().length() == 0 || product.getCategory().trim().length() < 1) {
            session.setAttribute("errorMsg", "Please select category for the product");
        } else if(ObjectUtils.isEmpty(productExisted)) {
            product.setImage(imageName);
            product.setDiscount(0);
            product.setDiscountPrice(product.getPrice());

            pservice.saveProduct(product);

            if(!image.isEmpty()) {
                // Đường dẫn tuyệt đối tới thư mục lưu trữ hình ảnh
                String uploadDir = "src/main/resources/static/img/product_img";
                Path path = Paths.get(uploadDir, image.getOriginalFilename());

                // Tạo thư mục nếu chưa tồn tại
                Files.createDirectories(path.getParent());
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            session.setAttribute("succMsg", "Saved Successfully");
        } else {
            session.setAttribute("errorMsg", "This product is already exists");
        }

        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "2") Integer pageSize) {
//        List<Product> productList = pservice.get
//        m.addAttribute("products", pservice.getAllProducts());

        Page<Product> pages = pservice.getAllProductPagination2(pageNo, pageSize);



        List<Product> products = pages.getContent();

        m.addAttribute("productsSize", products.size());

        m.addAttribute("products", products);
        m.addAttribute("pageNo", pages.getNumber());
        m.addAttribute("pageSize", pages.getSize());
        m.addAttribute("totalElements", pages.getTotalElements());
        m.addAttribute("totalPages", pages.getTotalPages());
        m.addAttribute("isFirst", pages.isFirst());
        m.addAttribute("isLast", pages.isLast());


        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") int id, HttpSession session) {

       Boolean deleteProduct = pservice.deleteProduct(id);
       if(deleteProduct) {
           session.setAttribute("succMsg", "Deleted Successfully");
       } else {
           session.setAttribute("errorMsg", "Not Deleted! Internal server error");
       }

       return "redirect:/admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String loadViewProducts(Model m, @PathVariable("id") int id) {

//        Product product = pservice.getProductById(id);
        m.addAttribute("product", pservice.getProductById(id));
        m.addAttribute("categories", service.getAllCategories());

        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, HttpSession session, @RequestParam("file") MultipartFile file) throws IOException {

        Product oldProduct = pservice.getProductById(product.getId());
        String fileName = file.isEmpty() ? oldProduct.getImage() : file.getOriginalFilename();


        if(product.getDiscount()<0 || product.getDiscount() > 100) {
            session.setAttribute("errorMsg", "Invalid Discount! !");
        } else {

        if(!ObjectUtils.isEmpty(product)) {
            oldProduct.setTitle(product.getTitle());
            oldProduct.setDescription(product.getDescription());
            oldProduct.setPrice(product.getPrice());
            oldProduct.setCategory(product.getCategory());
            oldProduct.setIsActive(product.getIsActive());
            oldProduct.setDiscount(product.getDiscount());
            oldProduct.setStock(product.getStock());

//            oldProduct.setDiscountPrice(product.getPrice()*(product.getDiscount()/100));

            Double discountP = (product.getPrice()*product.getDiscount())/100;
            Double discountPrice = product.getPrice()-discountP;
            oldProduct.setDiscountPrice(discountPrice);

            System.out.println();
            oldProduct.setImage(fileName);
        }

        Product newProduct = pservice.saveProduct(oldProduct);

        if(!ObjectUtils.isEmpty(newProduct) && !file.isEmpty()) {

            String uploadDir = "src/main/resources/static/img/product_img";
            Path path = Paths.get(uploadDir, file.getOriginalFilename());

            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            session.setAttribute("succMsg", "Updated Successfully");

        } else if(!ObjectUtils.isEmpty(newProduct) && file.isEmpty()) {
            session.setAttribute("succMsg", "Updated Successfully");
        } else {
            session.setAttribute("errorMsg", "Not Updated! Internal server error");
        }
        }
        return "redirect:/admin/editProduct/" + product.getId();


    }

    @GetMapping("/users")
    public String getAllUsers(Model m) {

      List<User> users = uservice.findUserByRole("ROLE_USER");
      m.addAttribute("users", users);
            return "admin/users";
    }

    @GetMapping("/admins")
    public String getAllAdmin(Model m) {

        List<User> admin = uservice.findUserByRole("ROLE_ADMIN");
        m.addAttribute("admin", admin);
        return "admin/admin";
    }

    @GetMapping("/updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {

        Boolean f = uservice.updateAccountStatus(id, status);
        if(f) {
            session.setAttribute("succMsg", "Account Status Updated Successfully");
        } else {
            session.setAttribute("errorMsg", "Not Updated! Internal server error");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/updateStsAd")
    public String updateAdAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {

        Boolean f = uservice.updateAccountStatus(id, status);
        if(f) {
            session.setAttribute("succMsg", "Account Status Updated Successfully");
        } else {
            session.setAttribute("errorMsg", "Not Updated! Internal server error");
        }
        return "redirect:/admin/admins";
    }

    @GetMapping("/orders")
    public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "2") Integer pageSize) {

        Page<ProductOrder> pages = oservice.getAllOrderIdPagination(pageNo, pageSize);
        List<ProductOrder> orders = pages.getContent();

//        m.addAttribute("orders", orders);
        m.addAttribute("srch", false);

        m.addAttribute("orderSize", orders.size());

        m.addAttribute("orders", orders);
        m.addAttribute("pageNo", pages.getNumber());
        m.addAttribute("pageSize", pages.getSize());
        m.addAttribute("totalElements", pages.getTotalElements());
        m.addAttribute("totalPages", pages.getTotalPages());
        m.addAttribute("isFirst", pages.isFirst());
        m.addAttribute("isLast", pages.isLast());


        return "admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) throws MessagingException, UnsupportedEncodingException {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for(OrderStatus orderStatus : values) {
            if(orderStatus.getId().equals(st)) {
                status = orderStatus.getName();
            }
        }

        ProductOrder updateOrder = oservice.updateOrderStatus(id, status);
        commonUtil.sendMailForProductOrder(updateOrder, status);



        if(!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "Status not updated");
        }

        return "redirect:/admin/orders";
    }

    @GetMapping("/search-order")
    public String searchOrder(@RequestParam("orderId") String orderId, Model model, HttpSession session
//                                @SessionAttribute("orders") List<ProductOrder> orders
    ) {

        if(!ObjectUtils.isEmpty(orderId.trim()) && orderId.trim().length() > 0) {

            ProductOrder order = oservice.getOrdersByOrderId(orderId.trim());

//            orders.stream().forEach(ok -> System.out.println(ok.getOrderId()));

            if (ObjectUtils.isEmpty(order)) {

                session.setAttribute("errorMsg", "Order not found!");
                model.addAttribute("orderOke", null);

            } else {
                model.addAttribute("orderOke", order);
            }

            model.addAttribute("srch", true);
        } else {
            model.addAttribute("srch", false);
            return "redirect:/admin/orders";
        }
        return "admin/orders";
    }

    @GetMapping("/search-product")
    public String searProduct(@RequestParam String keyword, Model model, HttpSession session) {

//        List<Product> products = pservice.searchProduct(keyword);
        List<Product> productList = pservice.searchProduct(keyword);

        model.addAttribute("products", productList);

        if(keyword.trim().isEmpty()) {
            return "redirect:/admin/products";
        }


//
//        if(!ObjectUtils.isEmpty(productList)) {
//            model.addAttribute("products", productList);
//        } else {
//            model.addAttribute("products", null);
////            session.setAttribute("errorMsg", "Product not found!");
//        }





        return "admin/products";
    }

    @GetMapping("/add-admin")
    public String adminAdd(Model model) {

        model.addAttribute("user", new User());

        return "admin/add_admin";
    }

    @PostMapping("/saveAdmin")
    public String saveUser(@Validated(Register.class) @ModelAttribute User user, BindingResult result , @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {

        if(result.hasErrors()) {
            return "admin/add_admin";
        }


        String imageName = !file.isEmpty() ? file.getOriginalFilename() : "default.jpg";
        user.setProfileImage(imageName);

        User exisUser = uservice.findUserByEmail(user.getEmail());

        if(ObjectUtils.isEmpty(exisUser)) {
            User savedUser = uservice.saveAdmin(user);

            if(ObjectUtils.isEmpty(savedUser)) {
                session.setAttribute("errorMsg", "Not saved! Internal server error");
            } else {
                if(!file.isEmpty()) {
                    // Đường dẫn tuyệt đối tới thư mục lưu trữ hình ảnh
                    String uploadDir = "src/main/resources/static/img/profile_img";
                    Path path = Paths.get(uploadDir, file.getOriginalFilename());

                    // Tạo thư mục nếu chưa tồn tại
                    Files.createDirectories(path.getParent());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }
                session.setAttribute("succMsg", "Registed Successfully");
            }
        } else {
            session.setAttribute("errorMsg", "This ADMIN is existed");
        }
        return "redirect:/admin/add-admin";
    }

    @GetMapping("/profile")
    public String profile() {
        return "admin/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user, @RequestParam MultipartFile img, HttpSession session) throws MessagingException, UnsupportedEncodingException {

        User user1r = uservice.updateUserProfile(user, img);
        if(ObjectUtils.isEmpty(user1r)) {
            session.setAttribute("errorMsg", "Profile update failed");
        } else {
            session.setAttribute("succMsg", "Profile updated successfully");
        }

        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String oldPassword, HttpSession session, Principal p) throws MessagingException, UnsupportedEncodingException {

        User loggedInUser = getLoggedInUserDetails(p);

        boolean matches = passwordEncoder.matches(oldPassword, loggedInUser.getPassword());

        if(matches) {
            session.setAttribute("succMsg", "Password changed successfully");
            loggedInUser.setPassword(newPassword);
            uservice.saveAdmin(loggedInUser);
        } else {
            session.setAttribute("errorMsg", "Current Password is incorrect");
        }

        return "redirect:/admin/profile";
    }

    private User getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();
        User user = uservice.findUserByEmail(email);

        return user;
    }

    @ExceptionHandler
    public String handleException(Exception ex) {
        return "error";
    }








}