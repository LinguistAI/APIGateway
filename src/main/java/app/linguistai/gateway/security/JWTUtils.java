package app.linguistai.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JWTUtils {
    private String accessSignKey = "broccoliisthegreatestfoodinthewholeuniverse";
    private String refreshSignKey = "pizzaisalsogoodbutnotreallygoodwithoutbroccoli"; //TODO move to application prop

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
        return extractAccessExpiration(token).before(new Date());
    }

    public Date extractRefreshExpiration(String token) {
        return extractAllClaims(token, refreshSignKey).getExpiration();
    }

    public String extractRefreshUsername(String token) {
        return extractAllClaims(token, refreshSignKey).getSubject();
    }

    public boolean isRefreshTokenExpired(String token) {
        return extractRefreshExpiration(token).before(new Date());
    }
}
