package org.hrimDev.signup.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrimDev.signup.dao.SignUpMapper;
import org.hrimDev.signup.service.SignUpService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final SignUpMapper signUpMapper;

    @Override
    public boolean checkDuplicateByUserId(String userId) throws Exception {
        return signUpMapper.countByUserId(userId) > 0;
    }
}
