package com.hrimDev.signup.serviceImpl;

import com.hrimDev.common.exception.DuplicateResourceException;
import com.hrimDev.signup.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hrimDev.signup.dao.SignUpMapper;
import com.hrimDev.signup.service.SignUpService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final PasswordEncoder passwordEncoder;

    private final SignUpMapper signUpMapper;

    @Override
    public boolean checkDuplicateByUserId(String userId) throws Exception {
        return signUpMapper.countByUserId(userId) > 0;
    }

    @Override
    public int signUpUserInfo(SignUpDto signUpDto) throws Exception {
        if(checkDuplicateByUserId(signUpDto.getUserId())) { //true면 중복
            throw new DuplicateResourceException("이미 존재하는 아이디입니다.");
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        signUpDto.setPassword(encodedPassword);
        return signUpMapper.signUpUserInfo(signUpDto);
    }
}
