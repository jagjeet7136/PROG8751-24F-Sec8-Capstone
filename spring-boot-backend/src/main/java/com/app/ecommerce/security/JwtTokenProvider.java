package com.app.ecommerce.security;

import com.app.ecommerce.constants.SecurityConstants;
import com.app.ecommerce.entity.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String jwtSecret;

    public JwtTokenProvider(@Value("${security.jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String generateToken(Long id, String fullName, String email, Authentication... authentication) {
        String userId;
        String username;

        if (authentication.length > 0) {
            User user = (User) authentication[0].getPrincipal();
            userId = String.valueOf(user.getId());
            username = user.getUsername();
            fullName = user.getUserFullName();
        } else {
            userId = String.valueOf(id);
            username = email;
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", username);
        claims.put("fullName", fullName);

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateTokenFromAuth(Authentication authentication) {
        return generateToken(null, null, null, authentication);
    }

    public String getJWTFromRequest(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(SecurityConstants.HEADER_STRING);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(SecurityConstants.TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.warn("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.warn("Malformed JWT token");
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string is empty");
        }
        return false;
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong((String) claims.get("id"));
    }
}
