import { api } from "@/config/axios/axios.js";
import { addToast } from "@/utils/toastBus.js"; // Toast 함수 임포트

export const axiosGet = (url, param) => {
  return api
    .get(url, param, { responseType: "json" })
    .then((res) => res)
    .catch((error) => {
      customError(error);
      return Promise.reject(error);
    });
};

export const axiosPost = (url, param) => {
  return api
    .post(url, param, { responseType: "json" })
    .then((res) => res)
    .catch((error) => {
      customError(error);
      return Promise.reject(error);
    });
};

const customError = (error) => {
  let message = "알 수 없는 오류가 발생했습니다.";
  if (error.response) {
    message =
      error.response.data?.errorMessage ||
      error.response.data?.error ||
      error.message;
  } else if (error.message) {
    message = error.message;
  }

  // Toast 표시
  addToast(message, "error");

  if (error.response?.status === 401) {
    console.warn("401 Unauthorized - 로그아웃 필요");
  }
};
