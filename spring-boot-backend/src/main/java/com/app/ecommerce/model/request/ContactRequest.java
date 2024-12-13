package com.app.ecommerce.model.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class ContactRequest {

    @NotBlank(message = "Username cannot be blank")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Message cannot be blank")
    private String message;

}
