package com.app.ecommerce.model.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserCreateRequest {

    @NotBlank(message = "full name is required.")
    @Size(min = 2, max = 100, message = "name should between 2 and 100 characters.")
    private String userFullName;

    @NotBlank(message = "Email address is required.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email address is invalid.")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100, message = "password should between 6 and 100 characters.")
    private String password;

    @NotBlank(message = "confirm password is required")
    @Size(min = 6, max = 100, message = "password should between 6 and 100 characters.")
    private String confirmPassword;

}
