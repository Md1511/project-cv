package com.alandha.shopping_cart.model;

import com.alandha.shopping_cart.configValidation.EmailValid;
import com.alandha.shopping_cart.configValidation.NameValid;
import com.alandha.shopping_cart.groupValidator.Register;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NameValid(groups = Register.class)
    private String name;

    @Size(min = 10, max = 10, message = "Your phone number must contain exactly 10 digits", groups = Register.class)
    @NotBlank(message = "Phone number can't be blank", groups = Register.class)
    @Pattern(regexp = "^[^\\s]+$", message = "Email cannot contain spaces", groups = Register.class)
    private String mobileNumber;

//    @Size(min = 10, max = 28, message = "Your email should have between 10 and 28 characters")
//    @NotBlank(message = "Email can't be blank")
//    @Pattern(regexp = "^[^\\s]+$", message = "Email cannot contain spaces")
    @EmailValid(groups = Register.class)
    private String email;

    @NotBlank(message = "Address can't be blank ", groups = Register.class)
    private String address;

    @NotBlank(message = "City can't be blank", groups = Register.class)
    private String city;

    @NotBlank(message = "State can't be blank", groups = Register.class)
    private String state;

    @NotBlank(message = "Pincode can't be blank", groups = Register.class)
    private String pincode;

//    @Size(min = 8, max = 44, message = "Your password should have between 8 and 44 characters")
    @Pattern(regexp = "^[^\\s]+$", message = "Email cannot contain spaces", groups = Register.class)
    @NotBlank(message = "Password can't be blank")
    private String password;

    private String profileImage;

    private String role;

    private Boolean isEnable;

    private Boolean accountNonLocked;

    private Integer failedAttempt;

    private Date lockTime;

    private String resetToken;
}
