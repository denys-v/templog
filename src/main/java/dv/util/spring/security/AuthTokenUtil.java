package dv.util.spring.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Utility class to compute and verify user authentication tokens (JWT-based).
 */
public class AuthTokenUtil {

    static class AuthTokenException extends Exception {

        public AuthTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final String authTokenSecret;

    /**
     * A constructor.
     *
     * @param authTokenSecret BASE64-encoded secret key to be used when creating auth tokens.
     */
    public AuthTokenUtil(String authTokenSecret) {
        this.authTokenSecret = authTokenSecret;
    }

    /**
     * Creates JWT-based auth token and weaves specified username into it.
     *
     * @param username to be encoded within a token.
     * @return the token.
     */
    public String buildToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, authTokenSecret)
                .compact();
    }

    /**
     * Verifies auth token and extracts username form it.
     *
     * @param token a token created via {@code buildToken} method.
     * @return extracted username (if the token is valid).
     * @throws AuthTokenException if the token can't be parsed successfully.
     */
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
