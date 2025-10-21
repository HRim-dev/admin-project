package com.hrimDev.login.serviceImpl;

import com.hrimDev.login.dao.LoginMapper;
import com.hrimDev.login.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService { /* stackoverflow 에러 해결*/

    private final LoginMapper loginMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {



        return User.builder()
                .username(username)
                .password(loginRequest.getPassword())
                .build();
    }
}
