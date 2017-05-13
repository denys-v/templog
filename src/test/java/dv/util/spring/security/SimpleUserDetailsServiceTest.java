package dv.util.spring.security;

import dv.dao.UserRepository;
import dv.model.Role;
import dv.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleUserDetailsServiceTest {

    private static final String EXISTING_USERNAME = "writer";
    private static final String EXISTING_PASSWORD = "password";
    private static final String NON_EXISTING_USERNAME = "test";

    private SimpleUserDetailsService simpleUserDetailsService;

    @Mock
    private UserRepository userRepo;

    @Before
    public void setUp() throws Exception {
        // given
        simpleUserDetailsService = new SimpleUserDetailsService(userRepo);

        User user = new User();
        user.setUsername(EXISTING_USERNAME);
        user.setPassword(EXISTING_PASSWORD);
        user.setRoles(Collections.singleton(Role.WRITER));

        when(userRepo.findByUsername(EXISTING_USERNAME)).thenReturn(user);
    }

    @Test
    public void shouldLoadUserByUsername() throws Exception {
        // when
        UserDetails userDetails = simpleUserDetailsService.loadUserByUsername(EXISTING_USERNAME);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(EXISTING_USERNAME);
        assertThat(userDetails.getPassword()).isEqualTo(EXISTING_PASSWORD);
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsOnly("ROLE_" + Role.WRITER.name());

    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldThrowExceptionOnUnknownUser() throws Exception {
        // when
        simpleUserDetailsService.loadUserByUsername(NON_EXISTING_USERNAME);
    }
}