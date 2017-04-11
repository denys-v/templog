package dv.config;

import dv.util.spring.security.AuthTokenFilter;
import dv.util.spring.security.AuthTokenUtil;
import dv.util.spring.security.SimpleUserDetailsService;
import dv.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
//@RestController
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final int BCRYPT_STRENGTH = 11;

    @Value("${dv.auth.token.header}")
    private String authTokenHeader;

    private final AuthTokenFilter authTokenFilter;
    private final AuthTokenUtil authTokenUtil;
    private final SimpleUserDetailsService simpleUserDetailsService;

    @Autowired
    public WebSecurityConfig(AuthTokenFilter authTokenFilter, AuthTokenUtil authTokenUtil, SimpleUserDetailsService simpleUserDetailsService) {
        this.authTokenFilter = authTokenFilter;
        this.authTokenUtil = authTokenUtil;
        this.simpleUserDetailsService = simpleUserDetailsService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(simpleUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .antMatchers("/templog/submit").hasRole(Role.WRITER.name())
                    .anyRequest().permitAll();

        http.formLogin()
                .successHandler((request, response, authentication) -> {
                    String token = authTokenUtil.buildToken(authentication.getName());
                    response.setHeader(this.authTokenHeader, token);
                })
                .failureHandler((request, response, exception) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
                });

        http.logout()
                .logoutSuccessHandler((request, response, authentication) -> {}); //.logoutSuccessUrl("/after_logout")

        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action not permitted");
        });

        // standard "remember me" disabled
        // TODO: implement "remember me" functionality within token-based security
//        http.rememberMe()
//                .rememberMeParameter("remember-me").rememberMeCookieName("remember-me")
//                .tokenValiditySeconds(5*60);

        // CSRF disabled because of token-based security usage
        http.csrf().disable();
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

//    /* Commented out - session usage replaced with JWT token security. */
//    @Bean
//    HeaderHttpSessionStrategy sessionStrategy() {
//        return new HeaderHttpSessionStrategy();
//    }
//
//    @GetMapping("/user_login")
//    public Map<String, String> userLogin(HttpSession session) {
//        return Collections.singletonMap("token", session.getId());
//    }
//
//    @RequestMapping(path = "/after_logout", method = RequestMethod.HEAD)
//    public void afterLogout() {
//         // no actions
//    }
}
