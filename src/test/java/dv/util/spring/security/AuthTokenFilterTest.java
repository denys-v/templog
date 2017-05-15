package dv.util.spring.security;

import dv.model.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthTokenFilterTest {

    private static final String AUTH_TOKEN_HEADER = "AUTH-TOKEN";
    private static final String AUTH_TOKEN = "token";
    private static final String USERNAME = "bob";

    private AuthTokenFilter authTokenFilter;

    @Mock
    private AuthTokenUtil authTokenUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;
    private SecurityContext previousSecurityContext;

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private FilterChain filterChain;

    @Before
    public void setUp() throws Exception {
        when(userDetails.getUsername()).thenReturn(USERNAME);
        when(userDetails.getPassword()).thenReturn(USERNAME + "_pass");

        when(userDetails.getAuthorities())
                .thenAnswer(invocation ->
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + Role.WRITER)));
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        previousSecurityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        authTokenFilter = new AuthTokenFilter(AUTH_TOKEN_HEADER, authTokenUtil, userDetailsService);
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.setContext(previousSecurityContext);
    }

    @Test
    public void shouldSetAuthenticationToken() throws Exception {
        // given
        when(httpServletRequest.getHeader(AUTH_TOKEN_HEADER)).thenReturn(AUTH_TOKEN);
        when(authTokenUtil.usernameFromToken(AUTH_TOKEN)).thenReturn(USERNAME); // as if the token is valid

        // when
        authTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(securityContext).setAuthentication(authenticationCaptor.capture());
        Authentication authentication = authenticationCaptor.getValue();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsOnly("ROLE_" + Role.WRITER);

        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void shouldSkipWhenNoAuthToken() throws Exception {
        // given
        when(httpServletRequest.getHeader(AUTH_TOKEN_HEADER)).thenReturn(null); // as if there is no token

        // when
        authTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(authTokenUtil, never()).usernameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any(Authentication.class));

        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void shouldRespondUnauthorizedOnInvalidToken() throws Exception {
        // given
        when(httpServletRequest.getHeader(AUTH_TOKEN_HEADER)).thenReturn(AUTH_TOKEN);
        when(authTokenUtil.usernameFromToken(AUTH_TOKEN))
                .thenThrow(new AuthTokenUtil.AuthTokenException("", null)); // as if the token is invalid

        // when
        authTokenFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

        // then
        verify(httpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
        verify(filterChain, never()).doFilter(httpServletRequest, httpServletResponse);
    }
}