package com.app.ecommerce.exceptions;

import com.app.ecommerce.enums.ErrorCode;
import com.app.ecommerce.model.dto.ApiErrorDTO;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public class ApiErrorFactory {
    public static ResponseEntity<ApiErrorDTO> buildErrorResponse(HttpServletRequest req, ErrorCode errorCode,
                                                                 List<String> errorsList) {
        ApiErrorDTO dto = buildErrorResponseHelper(req, errorCode, errorsList);
        return new ResponseEntity<>((dto), errorCode.getStatus());
    }

    public static ResponseEntity<ApiErrorDTO> buildErrorResponse(HttpServletRequest req, ErrorCode errorCode,
                                                                  Exception ex, List<String> errorsList) {
        ApiErrorDTO dto = buildErrorResponseHelper(req, errorCode, errorsList);
        dto.setMessage(ex.getMessage());
        return new ResponseEntity<>((dto), errorCode.getStatus());
    }

    public static ApiErrorDTO buildErrorResponseHelper(HttpServletRequest req, ErrorCode errorCode,
                                         List<String> errorsList) {
        ApiErrorDTO dto = new ApiErrorDTO();
        dto.setErrorCode(errorCode.getCode());
        dto.setMessage(errorCode.getMessage());
        dto.setStatus(errorCode.getStatus().value());
        dto.setPath(req.getRequestURI());
        dto.setTimestamp(LocalDateTime.now());
        dto.setErrors(errorsList);
        return dto;
    }
}