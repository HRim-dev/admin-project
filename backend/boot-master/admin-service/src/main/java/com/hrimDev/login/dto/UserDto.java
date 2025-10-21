package org.hrimDev.login.dto;

/**
 * 사용자 정보 DTO
 */
public class UserDto {
    private Long id;          //회원 고유 번호
    private String userId;    //로그인 ID
    private String password;  //로그인 비밀번호(암호화)
    private String name;      //사용자 이름
}
