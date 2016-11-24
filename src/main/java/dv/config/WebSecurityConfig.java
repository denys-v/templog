package dv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

@EnableWebSecurity
@RestController
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("reader").password("reader").roles("READER").and()
                .withUser("writer").password("writer").roles("WRITER", "READER").and()
                .withUser("lelya").password("lel_0926").roles("WRITER", "READER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                    .antMatchers("/templog/submit").hasRole("WRITER")
//                    .anyRequest().permitAll()//.hasRole("READER")
//                .and()
//                .httpBasic()
//                .and()
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())//.disable()
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .logout().disable();

        http.authorizeRequests()
                    .antMatchers("/templog/submit").hasRole("WRITER")
                    .anyRequest().permitAll();

        http.formLogin()
                .successHandler((request, response, authentication) -> {})
                .failureHandler((request, response, exception) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
                });

        http.logout()
                .logoutSuccessHandler((request, response, authentication) -> {}); //.logoutSuccessUrl("/after_logout")

        http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action not permitted");
        });

        http.rememberMe()
                .rememberMeParameter("remember-me").rememberMeCookieName("remember-me")
                .tokenValiditySeconds(5*60);

        http.csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

    @Bean
    HeaderHttpSessionStrategy sessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }

//    @GetMapping("/user_login")
//    public Map<String, String> userLogin(HttpSession session) {
//        return Collections.singletonMap("token", session.getId());
//    }
//
    @RequestMapping(path = "/after_logout", method = RequestMethod.HEAD)
    public void afterLogout() {
        // no actions
    }
}
