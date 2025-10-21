import { axiosPost, axiosGet } from "@/config/axios-config";

// 로그인
export function login(input) {
  return axiosPost("/api/login", input);
}

// 로그아웃
export function logout(input) {
  return axiosPost("/api/logout", input);
}