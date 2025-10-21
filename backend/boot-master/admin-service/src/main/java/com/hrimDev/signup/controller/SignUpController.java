package org.hrimDev.signup.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrimDev.signup.service.SignUpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignUpController {

    private final SignUpService signUpService;
    @GetMapping("/double-check")
    public ResponseEntity<Object> doubleCheckId(@RequestParam String userId) throws Exception {
        boolean isDuplicate = signUpService.checkDuplicateByUserId(userId);
        return new ResponseEntity<>(isDuplicate, HttpStatus.OK);
    }
}
