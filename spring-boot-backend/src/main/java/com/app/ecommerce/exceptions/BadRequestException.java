package com.app.ecommerce.exceptions;

import com.app.ecommerce.enums.ErrorCode;

public class BadRequestException extends ApiException {
    public BadRequestException(String msg) {
        super(msg, ErrorCode.VALIDATION_ERROR);
    }
}
