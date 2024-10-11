package com.app.ecommerce.model.response;

import lombok.Data;

@Data
public class InvalidLoginResponse {
    private String userName;
    private String password;

    public InvalidLoginResponse() {
        this.userName = "Invalid Username";
        this.password = "Invalid Password";
    }
}
