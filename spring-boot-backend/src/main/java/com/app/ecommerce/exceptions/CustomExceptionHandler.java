package com.app.ecommerce.exceptions;

import com.app.ecommerce.model.dto.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errorDetails = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorDetails.add(fieldError.getDefaultMessage());
        }
        ApiErrorDTO errorResponse = handleAllExceptions(ex);
        errorResponse.setStatus(HttpStatus.BAD_REQUEST);
        errorResponse.setErrors(errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorDTO> handleValidationException(HttpServletRequest req, ValidationException ex) {
        ApiErrorDTO apiErrorDTO = handleAllExceptions(ex);
        apiErrorDTO.setStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFoundException(HttpServletRequest req, NotFoundException ex) {
        ApiErrorDTO apiErrorDTO = handleAllExceptions(ex);
        apiErrorDTO.setStatus(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorDTO> handleForbiddenException(HttpServletRequest req, ForbiddenException ex) {
        ApiErrorDTO apiErrorDTO = handleAllExceptions(ex);
        apiErrorDTO.setStatus(HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiErrorDTO> handleDateTimeParseException(HttpServletRequest req, NotFoundException ex) {
        ApiErrorDTO apiErrorDTO = handleAllExceptions(ex);
        apiErrorDTO.setStatus(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiErrorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorDTO> handleBindException(BindException bindException){
        BindingResult bindingResult = bindException.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errorDetails = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errorDetails.add(fieldError.getDefaultMessage());
        }
        ApiErrorDTO errorResponse = handleAllExceptions(bindException);
        errorResponse.setStatus(HttpStatus.BAD_REQUEST);
        errorResponse.setErrors(errorDetails);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private static ApiErrorDTO handleAllExceptions(Exception ex) {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO.setMessage(ex.getMessage());
        apiErrorDTO.setErrors(new ArrayList<>());
        apiErrorDTO.setTime(LocalDateTime.now());
        return apiErrorDTO;
    }

}
