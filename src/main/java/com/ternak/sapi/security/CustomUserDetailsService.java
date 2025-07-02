package com.ternak.sapi.security;

import com.ternak.sapi.exception.ApiException;
import com.ternak.sapi.exception.ResourceNotFoundException;
import com.ternak.sapi.model.User;
import com.ternak.sapi.repository.UserRepository;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // Let people login with either username or email
        User user = null;
        try {
            user = Optional.ofNullable(userRepository.findByUsername(username))
                    .orElseThrow(() ->
                            new ApiException(HttpStatus.BAD_REQUEST_400,"User not found with username or email : " + username)
            );
        } catch (IOException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST_400,"User not found with username or email : " + username);

        }

        return UserPrincipal.create(user);
    }
    public UserDetails loadUserById(String id) throws IOException {
        User user = Optional.ofNullable(userRepository.findById(id.toString())).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
}