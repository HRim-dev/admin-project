// src/config/axios.js
import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,  // 쿠키나 인증정보 포함할 때 필요
});


export { axios, api };
