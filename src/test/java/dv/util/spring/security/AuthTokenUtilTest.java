package dv.util.spring.security;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthTokenUtilTest {

    private final static String SECRET_KEY = "secret";

    private AuthTokenUtil authTokenUtil = new AuthTokenUtil(SECRET_KEY);

    @Test
    public void shouldEncodeAndDecodeUsername() throws Exception {
        // given
        String username = "bob";

        // when
        String token = authTokenUtil.buildToken(username);
        String decodedUsername = authTokenUtil.usernameFromToken(token);

        // then
        assertThat(decodedUsername).isEqualTo(username);
    }

    @Test(expected = AuthTokenUtil.AuthTokenException.class)
    public void shouldThrowExceptionOnInvalidToken() throws Exception {
        authTokenUtil.usernameFromToken("invalidtoken");
    }
}