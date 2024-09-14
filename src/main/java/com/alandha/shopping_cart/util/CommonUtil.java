package com.alandha.shopping_cart.util;

import com.alandha.shopping_cart.model.ProductOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Component
public class CommonUtil {


    @Autowired
    private JavaMailSender mailSender;


    public Boolean sendMail(String url, String receipentEmail) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("minhhn.23ns@vku.udn.vn", "Shopping Cart");
        helper.setTo(receipentEmail);

        String content = "<p>Hello,</p>" + "<p>You have requested to reset your password. </p>" +
                "<p>Click the link below to change your password: </p>" + "<p><a href=\"" + url
                + "\"> Change my password </a></p>";

        helper.setSubject("Password Reset");
        helper.setText(content, true);

        mailSender.send(message);

        return true;
    }

    public static String generateUrl(HttpServletRequest request) {

//        http://localhost:/forgot-password
         String siteUrl = request.getRequestURL().toString();
         return siteUrl.replace(request.getServletPath(), "");
    }

    String msg = null;

    public Boolean sendMailForProductOrder(ProductOrder productOrder, String status) throws MessagingException, UnsupportedEncodingException {

        msg = "<p>Hello <b>[[name]]<b/></p>,<p>Thank you for your order!, Your order: <b>[[orderStatus]]</b></p>" +
                "<p><b>Product Details : </b></p>" +
                "<p>Name : [[productName]]</p>" +
                "<p> Category : [[category]]</p>" +
                "<p> Quantity : [[quantity]]</p>" +
                "<p> Price : [[price]]</p>" +
                "<p> Payment Type : [[paymentType]]</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("minhhn.23ns@vku.udn.vn", "Shopping Cart");
        helper.setTo(productOrder.getOrderAddress().getEmail());

        msg = msg.replace("[[name]]", productOrder.getOrderAddress().getFirstName() + " " + productOrder.getOrderAddress().getLastName());
        msg = msg.replace("[[orderStatus]]", status);
        msg = msg.replace("[[productName]]", productOrder.getProduct().getTitle());
        msg = msg.replace("[[category]]", productOrder.getProduct().getCategory());
        msg = msg.replace("[[quantity]]", productOrder.getQuantity().toString());
        msg = msg.replace("[[price]]", productOrder.getPrice().toString());
        msg = msg.replace("[[paymentType]]", productOrder.getPaymentType());




        helper.setSubject("Product Order Status");
        helper.setText(msg, true);
        mailSender.send(message);

        return true;
    }


}
