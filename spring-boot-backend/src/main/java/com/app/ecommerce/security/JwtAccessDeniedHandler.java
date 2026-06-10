package com.app.ecommerce.security;

import com.app.ecommerce.enums.ErrorCode;
import com.app.ecommerce.model.dto.ApiErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO.setStatus(ErrorCode.ACCESS_DENIED.getStatus().value());
        apiErrorDTO.setErrorCode(ErrorCode.ACCESS_DENIED.getCode());
        apiErrorDTO.setMessage(ErrorCode.ACCESS_DENIED.getMessage());
        apiErrorDTO.setErrors(Collections.singletonList(accessDeniedException.getMessage()));
        apiErrorDTO.setPath(request.getRequestURI());
        apiErrorDTO.setTimestamp(LocalDateTime.now());
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        response.getWriter().write(objectMapper.writeValueAsString(apiErrorDTO));
    }
}
