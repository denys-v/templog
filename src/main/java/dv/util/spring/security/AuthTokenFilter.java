package dv.util.spring.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Value("${dv.auth.token.header}")
    private String authTokenHeader;

    private AuthTokenUtil authTokenUtil;
    private UserDetailsService userDetailsService;

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

    @Autowired
    public void setAuthTokenUtil(AuthTokenUtil authTokenUtil) {
        this.authTokenUtil = authTokenUtil;
    }

    @Autowired
    void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
