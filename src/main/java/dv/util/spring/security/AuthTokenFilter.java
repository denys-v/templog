package dv.util.spring.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter to check if the request contains a header with authentication token - then validate the token,
 * find corresponding user and initialize SecurityContext accordingly.
 * If the token provided is invalid - respond with HTTP 401 (Unauthorized) status.
 * If no token provided - simply pass through to filter chain.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final String authTokenHeader;
    private final AuthTokenUtil authTokenUtil;
    private final UserDetailsService userDetailsService;

    public AuthTokenFilter(String authTokenHeader, AuthTokenUtil authTokenUtil, UserDetailsService userDetailsService) {
        this.authTokenHeader = authTokenHeader;
        this.authTokenUtil = authTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = request.getHeader(this.authTokenHeader);
        if (StringUtils.isNotBlank(authToken)) {
            log.info("Authentication token: {}", authToken);

            String username;
            try {
                username = authTokenUtil.usernameFromToken(authToken);
            } catch (AuthTokenUtil.AuthTokenException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }

        filterChain.doFilter(request, response);
    }
}
