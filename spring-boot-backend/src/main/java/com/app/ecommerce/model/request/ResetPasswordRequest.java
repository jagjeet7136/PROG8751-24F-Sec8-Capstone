package com.app.ecommerce.model.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String securityAnswer;
    private String newPassword;
}
