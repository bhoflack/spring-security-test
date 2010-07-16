package com.melexis;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DelegateUserServiceTest {

    private DelegateUserDetailsService delegateUserService;

    private UserDetailsService details1;
    private UserDetailsService details2;
    private User expectedUser;
    private String username;

    @Before
    public void setUp() {
        details1 = createMock(UserDetailsService.class);
        details2 = createMock(UserDetailsService.class);

        final List<UserDetailsService> services = new ArrayList<UserDetailsService>();
        services.add(details1);
        services.add(details2);

        delegateUserService = new DelegateUserDetailsService(services);

        username = "brh";
        expectedUser = new User(username, "", true, new GrantedAuthority[] {});
    }

    @Test
    public void loadUserByUsername() {
        expect(details1.loadUserByUsername(username)).andReturn(expectedUser);

        replay(details1, details2);
        assertEquals(expectedUser, delegateUserService.loadUserByUsername(username));
        verify(details1, details2);
    }

    @Test
    public void notFoundInFirstService() {
        expect(details1.loadUserByUsername(username)).andThrow(new UsernameNotFoundException("blaat"));
        expect(details2.loadUserByUsername(username)).andReturn(expectedUser);

        replay(details1, details2);
        assertEquals(expectedUser, delegateUserService.loadUserByUsername(username));
        verify(details1, details2);
    }

    @Test(expected=UsernameNotFoundException.class)
    public void noUserFound() {
        expect(details1.loadUserByUsername(username)).andThrow(new UsernameNotFoundException("blaat"));
        expect(details2.loadUserByUsername(username)).andThrow(new UsernameNotFoundException("blaat"));

        replay(details1, details2);
        assertNull(delegateUserService.loadUserByUsername(username));
        verify(details1, details2);
    }
}
