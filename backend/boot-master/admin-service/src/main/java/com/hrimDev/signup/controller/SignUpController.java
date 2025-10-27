package com.hrimDev.signup.controller;

import com.hrimDev.signup.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hrimDev.signup.service.SignUpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignUpController {

    private final SignUpService signUpService;

    /**
     * 아이디 중복확인
     * @param userId
     * @return
     * @throws Exception
     */
    @GetMapping("/double-check")
    public ResponseEntity<Object> doubleCheckId(@RequestParam String userId) throws Exception {
        boolean isDuplicate = signUpService.checkDuplicateByUserId(userId);
        return new ResponseEntity<>(isDuplicate, HttpStatus.OK);
    }

    /**
     * 회원가입
     */
    @PostMapping("/info")
    public ResponseEntity<Object> signUpUserInfo(@RequestBody SignUpDto signUpDto) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int insertResult = signUpService.signUpUserInfo(signUpDto);
        if(insertResult>0){
            resultMap.put("scss_f", "Y");
        }else{
            resultMap.put("scss_f", "N");
        }
        return new ResponseEntity<>(resultMap, HttpStatus.OK);

    }
}
