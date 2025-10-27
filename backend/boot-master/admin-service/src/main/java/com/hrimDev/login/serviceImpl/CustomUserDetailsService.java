package com.hrimDev.login.serviceImpl;

import com.hrimDev.login.dao.LoginMapper;
import com.hrimDev.login.dto.LoginRequest;
import com.hrimDev.login.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LoginMapper loginMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        LoginRequest user = loginMapper.getLoginRequest(userId);

        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 권한(roles)을 지정 - 실제로는 DB에서 가져오거나 enum으로 관리 가능
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return User.builder()
                .username(user.getUserId())
                .password(user.getPassword()) // 비밀번호는 암호화되어 있어야 함
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
