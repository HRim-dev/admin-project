import { axiosPost, axiosGet } from "@/config/axios-config";

// 소속부서명 조회
export function getDeptOptions() {
  return axiosGet("/api/signup/get/deptNm");
}

//아이디 중복확인
export function doubleCheckId(input) {
  return axiosGet("/api/signup/double-check", input);
}

//회원가입
export function signUpUser(input) {
  return axiosPost("/api/signup/info", input);
}
