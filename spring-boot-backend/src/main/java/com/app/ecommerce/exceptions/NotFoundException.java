package com.app.ecommerce.exceptions;

import com.app.ecommerce.enums.ErrorCode;

public class NotFoundException extends ApiException {
    public NotFoundException(String msg) {
        super(msg, ErrorCode.RESOURCE_NOT_FOUND);
    }
}
