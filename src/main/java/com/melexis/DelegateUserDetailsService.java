package com.melexis;

import org.springframework.dao.DataAccessException;
import org.springframework.security.AuthenticationException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import java.util.List;

public class DelegateUserDetailsService implements UserDetailsService{

    private final List<UserDetailsService> services;

    public DelegateUserDetailsService(final List<UserDetailsService> services) {
        this.services = services;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        System.out.println("GOT request for user " + username);

        String message = null;

        for (UserDetailsService service : services) {
            System.out.println("Trying service " + service + " " + username);

            try {
                final UserDetails details = service.loadUserByUsername(username);
                if (details != null) {
                    return details;
                }
            } catch (AuthenticationException e) {
                // ignore the exception and try on a different service
                message = e.getMessage();
            }
        }

        throw new UsernameNotFoundException(message);
    }
}
