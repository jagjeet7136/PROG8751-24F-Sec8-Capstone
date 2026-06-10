package com.app.ecommerce.exceptions;

import com.app.ecommerce.enums.ErrorCode;

public class ConflictException extends ApiException {
    public ConflictException(String msg) {
        super(msg, ErrorCode.RESOURCE_CONFLICT);
    }
}
