// src/config/axios-config.js
import { api, axios } from "@/config/axios/axios.js"; // @는 src를 가리키도록 설정해야 함

// GET 요청 처리
export const axiosGet = (url, param) => {
  return api
    .get(url, param, {
      responseType: "json",
    })
    .then((response) => {
      return response;
    })
    .catch((error) => {
      customError(error);
      return Promise.reject(error);
    });
};

// POST 요청 처리
export const axiosPost = (url, param) => {
  return api
    .post(url, param, {
      responseType: "json",
    })
    .then((res) => {
      return res;
    })
    .catch((error) => {
      customError(error);
      return Promise.reject(error);
    });
};

export const axiosPostNoResponse = (url, param) => {
  const config = {
    headers: {
      "Content-Type": "application/json",
    },
    timeout: 0,
    responseType: "stream",
  };
  return api.post(url, param, config).catch((error) => {
    console.log("loggin response is canceled", error);
    return Promise.resolve();
  });
};

// 커스텀 에러 처리
const customError = (error) => {
  let errorMessage = "An unknown error occurred";
  let errorStatus = 0;

  if (error.response) {
    errorMessage = error.response.data?.error || error.response.statusText;
    errorStatus = error.response.status;
  } else if (error.message) {
    errorMessage = error.message;
  }

  // \n을 <br>로 바꾸고 alert 사용
  let formattedMessage = errorMessage.replace(/\n/g, "\n");
  window.alert(`Error: ${formattedMessage}`);

  // 401 처리: 인증 실패 시 로그아웃
  if (errorStatus === 401) {
    try {
    } catch (error) {
      console.error("Router error:", error);
    }
  }
};
