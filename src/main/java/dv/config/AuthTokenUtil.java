package dv.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenUtil {

    static class AuthTokenException extends Exception {

        public AuthTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Value("${dv.auth.token.secret}")
    private String authTokenSecret;

    public String buildToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, authTokenSecret)
                .compact();
    }

    public String usernameFromToken(String token) throws AuthTokenException {
        try {
            return Jwts.parser()
                    .setSigningKey(authTokenSecret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new AuthTokenException("Error when extracting username from auth token: " + e.getMessage(), e);
        }
    }
}
