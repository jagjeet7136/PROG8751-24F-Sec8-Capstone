package com.app.ecommerce.exceptions;

import com.app.ecommerce.enums.ErrorCode;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String msg) {
        super(msg, ErrorCode.ACCESS_DENIED);
    }
}
