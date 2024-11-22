package com.app.ecommerce.exceptions;

import com.app.ecommerce.model.dto.ApiErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO.setStatus(HttpStatus.FORBIDDEN);
        apiErrorDTO.setMessage("You do not have permission to access this resource.");
        apiErrorDTO.setErrors(Collections.singletonList(accessDeniedException.getMessage()));
        apiErrorDTO.setTime(LocalDateTime.now());

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        response.getWriter().write(objectMapper.writeValueAsString(apiErrorDTO));
    }
}
