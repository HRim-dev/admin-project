package org.hrimDev.signup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {
    private Long id;          //회원 고유 번호
    private String userId;    //로그인 ID
    private String password;  //로그인 비밀번호(암호화)
    private String name;      //사용자 이름
}
