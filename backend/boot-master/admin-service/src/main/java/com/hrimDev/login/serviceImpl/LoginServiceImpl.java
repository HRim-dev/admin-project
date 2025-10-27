package com.hrimDev.login.serviceImpl;

import com.hrimDev.jwt.dto.TokenDto;
import com.hrimDev.jwt.service.JwtService;
import com.hrimDev.login.dao.LoginMapper;
import com.hrimDev.login.dto.LoginRequest;
import com.hrimDev.login.dto.LoginResponse;
import com.hrimDev.login.dto.UserDto;
import com.hrimDev.login.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final LoginMapper loginMapper;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String userId = loginRequest.getUserId();
        String password = loginRequest.getPassword();

        validateCredentials(userId, password);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userId, password)
            );

            // 인증 성공 처리
            SecurityContextHolder.getContext().setAuthentication(authentication);
            resetLoginFailCount(userId); // 실패 카운트 초기화

            String accessToken = jwtService.createAccessToken(authentication);
            UserDto userInfo = loginMapper.getLoginUserInfo(userId);

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .userDto(userInfo)
                    .build();

        } catch (BadCredentialsException ex) {
            handleLoginFailure(userId);
            throw ex; // 메시지는 handleLoginFailure 내부에서 던집니다.
        } catch (StackOverflowError e) {
            log.error("StackOverflowError 발생: AuthenticationManager 순환 참조 가능성", e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("로그인 처리 중 오류가 발생했습니다.", e);
        }
    }

    //자격증명 유효성 검증
    private void validateCredentials(String userId, String password) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BadCredentialsException("아이디를 입력해주세요.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BadCredentialsException("비밀번호를 입력해주세요.");
        }
    }

    //로그인 실패 처리
    private void handleLoginFailure(String userId) {
        try {
            increaseLoginFailCount(userId);
            int failCount = getLoginFailCount(userId);

            if (failCount >= 5) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "로그인 실패 5회로 계정이 차단되었습니다.");
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다. (실패 %d회 / 최대 5회)", failCount)
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("로그인 실패 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public int getLoginFailCount(String userId) throws Exception {
        Integer failCount = loginMapper.getLoginFailCount(userId);
        if(failCount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않은 사용자입니다.");
        }

        return failCount.intValue() ;
    }


    @Override
    public void increaseLoginFailCount(String userId) throws Exception {
        loginMapper.increaseLoginFailCount(userId);
    }

    @Override
    public void resetLoginFailCount(String managerId) throws Exception {
        loginMapper.resetLoginFailCount(managerId);
    }


    @Override
    public String checkDeleteUser(HttpServletResponse response, String managerId) throws Exception {
        return "";
    }
}
