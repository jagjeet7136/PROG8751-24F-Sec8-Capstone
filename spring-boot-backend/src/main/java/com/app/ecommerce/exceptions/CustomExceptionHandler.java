package com.app.ecommerce.exceptions;

import com.app.ecommerce.enums.ErrorCode;
import com.app.ecommerce.model.dto.ApiErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorDTO> handleApiException(HttpServletRequest req, ApiException ex) {
        return ApiErrorFactory.buildErrorResponse(req, ex.getErrorCode(), ex, new ArrayList<>());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationsExceptions(HttpServletRequest req,
                                                                  MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errorDetails = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorDetails.add(fieldError.getDefaultMessage());
        }
        return ApiErrorFactory.buildErrorResponse(req, ErrorCode.VALIDATION_ERROR, errorDetails);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorDTO> handleBindException(HttpServletRequest req, BindException bindException){
        BindingResult bindingResult = bindException.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errorDetails = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorDetails.add(fieldError.getDefaultMessage());
        }
        return ApiErrorFactory.buildErrorResponse(req, ErrorCode.VALIDATION_ERROR, errorDetails);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolationException(HttpServletRequest req,
                                                                          ConstraintViolationException ex) {
        List<String> errorDetails = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        return ApiErrorFactory.buildErrorResponse(req, ErrorCode.VALIDATION_ERROR, errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(HttpServletRequest req, Exception ex) {
        return ApiErrorFactory.buildErrorResponse(req, ErrorCode.INTERNAL_SERVER_ERROR, new ArrayList<>());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleBadCredentials(
            HttpServletRequest req,
            BadCredentialsException ex) {
        return ApiErrorFactory.buildErrorResponse(req, ErrorCode.BAD_CREDENTIALS, new ArrayList<>());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDTO> handleAccessDeniedException(
            HttpServletRequest req, AccessDeniedException ex) {
        return ApiErrorFactory.buildErrorResponse(req, ErrorCode.ACCESS_DENIED, new ArrayList<>());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiErrorDTO> handleDisabledException(
            HttpServletRequest req, DisabledException ex) {
        return ApiErrorFactory.buildErrorResponse(req, ErrorCode.ACCOUNT_DISABLED, new ArrayList<>());
    }

}