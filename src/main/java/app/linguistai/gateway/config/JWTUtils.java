package app.linguistai.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JWTUtils {
    @Value("${spring.jwt.access.key}")
    private String accessSignKey;

    @Value("${spring.jwt.refresh.key}")
    private String refreshSignKey;

    private Claims extractAllClaims(String token, String signKey)  {
        SecretKey key = Keys.hmacShaKeyFor(signKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
            .setSigningKey(key)   
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public Date extractAccessExpiration(String token) {
        return extractAllClaims(token, accessSignKey).getExpiration();
    }

    public String extractAccessUsername(String token) {
        return extractAllClaims(token, accessSignKey).getSubject();
    }

    public boolean isAccessTokenExpired(String token) {
        try {
            return extractAccessExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Date extractRefreshExpiration(String token) {
        return extractAllClaims(token, refreshSignKey).getExpiration();
    }

    public String extractRefreshUsername(String token) {
        return extractAllClaims(token, refreshSignKey).getSubject();
    }

    public boolean isRefreshTokenExpired(String token) {
        try {
            return extractRefreshExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }        
    }
}
