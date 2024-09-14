package com.alandha.shopping_cart.controller;

import com.alandha.shopping_cart.model.Cart;
import com.alandha.shopping_cart.model.OrderRequest;
import com.alandha.shopping_cart.model.ProductOrder;
import com.alandha.shopping_cart.model.User;
import com.alandha.shopping_cart.service.CartService;
import com.alandha.shopping_cart.service.OrderService;
import com.alandha.shopping_cart.service.UserService;
import com.alandha.shopping_cart.util.CommonUtil;
import com.alandha.shopping_cart.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
@SessionAttributes({"totalOrderPrice"})
public class UserController {


    @Autowired
    private UserService uService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/")
    public String home() {
        return "user/home";
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
        Cart saveCart = cartService.saveCart(pid, uid);

        if(ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Product add to cart failed");
        } else {
            session.setAttribute("succMsg", "Product added to cart successfully");
        }

        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal principal, Model model) {


        User user = getLoggedInUserDetails(principal);
        List<Cart> cartList = cartService.getCartByUserId(user.getId());
        model.addAttribute("carts", cartList);

        if(cartList.size() > 0) {
            Double totalOrderPrice = cartList.get(cartList.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }


        return "/user/cart";
    }


    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid, HttpSession session) {

        cartService.updateQuantity(sy, cid);

        return "redirect:/user/cart";
    }

    @GetMapping("/orders")
    public String orderPage(@SessionAttribute("totalOrderPrice") Double oke, Model model) {

        Double tax = oke*0.1;

        Double finalTotalOrderPrice = oke+25+tax;

        model.addAttribute("finalTotalOrderPrice", finalTotalOrderPrice);
        model.addAttribute("tax", tax);

        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws MessagingException, UnsupportedEncodingException {
        System.out.println(request);

        User user = getLoggedInUserDetails(p);

        orderService.saveOrder(user.getId(), request);


        return "redirect:/user/success";

    }

    @GetMapping("/success")
    public String loadSuccess() {
        return "/user/success";
    }

    @GetMapping("/user-orders")
    public String myOrder(Model m, Principal p) {

        User loginUser = getLoggedInUserDetails(p);

        List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());

        m.addAttribute("orders", orders);

        return "/user/my_order";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) throws MessagingException, UnsupportedEncodingException {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for(OrderStatus orderStatus : values) {
            if(orderStatus.getId().equals(st)) {
                status = orderStatus.getName();
            }
        }

        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
        commonUtil.sendMailForProductOrder(updateOrder, status);

        if(!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "Status not updated");
        }

        return "redirect:/user/user-orders";
    }


    @GetMapping("/profile")
    public String profile() {
        return "/user/profile";
    }


    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user, @RequestParam MultipartFile img, HttpSession session) throws MessagingException, UnsupportedEncodingException {

        User user1r = uService.updateUserProfile(user, img);
        if(ObjectUtils.isEmpty(user1r)) {
            session.setAttribute("errorMsg", "Profile update failed");
        } else {
            session.setAttribute("succMsg", "Profile updated successfully");
        }

        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String oldPassword, HttpSession session, Principal p) throws MessagingException, UnsupportedEncodingException {
        User loggedInUser = getLoggedInUserDetails(p);

        boolean matches = passwordEncoder.matches(oldPassword, loggedInUser.getPassword());

        if(matches) {
            session.setAttribute("succMsg", "Password changed successfully");
            loggedInUser.setPassword(newPassword);
            uService.saveUser(loggedInUser);
        } else {
            session.setAttribute("errorMsg", "Current Password is incorrect");
        }

        return "redirect:/user/profile";
    }








    private User getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();
        User user = uService.findUserByEmail(email);

        return user;
    }
}
