package com.app.todolist.configurations;

import com.app.todolist.constants.SecurityConstants;
import com.app.todolist.entity.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    public String generateToken(Long id, String name, String email, Authentication... authentication) {
        String userId;
        String username;
        String fullName;
        if(authentication.length>0) {
            User user = (User) authentication[0].getPrincipal();
            userId = Long.toString(user.getId());
            username = user.getUsername();
            fullName = user.getUserFullName();
        }
        else {
            userId = Long.toString(id);
            username = email;
            fullName = name;
        }
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(System.currentTimeMillis()+ SecurityConstants.EXPIRATION_TIME);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", username);
        claims.put("fullName", fullName);

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
    }

    public String helperGenerateToken(Authentication authentication) {
        return generateToken(null, null, null, authentication);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token);
            return true;
        }
        catch (SignatureException ex) {
            System.out.println("Invalid JWT Signature");
        }
        catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT Token");
        }
        catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT Token");
        }
        catch (IllegalArgumentException ex) {
            System.out.println("Jwt claims string is empty");
        }
        return false;
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token).getBody();
        String id = (String)claims.get("id");
        return Long.parseLong(id);
    }
}
