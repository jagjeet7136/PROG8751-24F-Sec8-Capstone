package com.app.ecommerce.security;

import com.app.ecommerce.enums.ErrorCode;
import com.app.ecommerce.model.dto.ApiErrorDTO;
import com.app.ecommerce.model.response.InvalidLoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

//        InvalidLoginResponse loginResponse = new InvalidLoginResponse();
//        String jsonLoginResponse = new ObjectMapper().writeValueAsString(loginResponse);
//        response.setContentType("application/json");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.getWriter().print(jsonLoginResponse);

        ApiErrorDTO apiErrorDTO = new ApiErrorDTO();
        apiErrorDTO.setStatus(ErrorCode.BAD_CREDENTIALS.getStatus().value());
        apiErrorDTO.setErrorCode(ErrorCode.BAD_CREDENTIALS.getCode());
        apiErrorDTO.setMessage(ErrorCode.BAD_CREDENTIALS.getMessage());
        apiErrorDTO.setErrors(Collections.singletonList(authException.getMessage()));
        apiErrorDTO.setPath(request.getRequestURI());
        apiErrorDTO.setTimestamp(LocalDateTime.now());
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        response.getWriter().write(objectMapper.writeValueAsString(apiErrorDTO));
    }
}
