<template>
  <div class="login-container">
    <h2>로그인</h2>

    <!-- 오류 메시지 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 입력 필드 -->
    <input v-model="id" type="text" placeholder="아이디" />
    <input v-model="password" type="password" placeholder="비밀번호" />

    <!-- 로그인 버튼 -->
    <button class="login-btn" @click="clickLoginBtn">로그인</button>

    <!-- 추가 기능 -->
    <!-- <div class="extra-buttons">
      <button class="link-btn" @click="goFindId">아이디 찾기</button>
      <span class="divider">|</span>
      <button class="link-btn" @click="goFindPassword">비밀번호 찾기</button>
    </div> -->

    <div class="signup-section">
      <span>계정이 없으신가요?</span>
      <button class="signup-btn" @click="goSignup">회원가입</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { login } from '@/api/login';
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";

import { addToast } from "@/utils/toastBus.js"; // Toast 함수 임포트
const route = useRoute();
const router = useRouter();

const id = ref('')
const password = ref('')
const error = ref('')

const userStore = useUserStore();

const clickLoginBtn = () => {
  error.value = ''
  if (!id.value) {
    error.value = '아이디를 입력해주세요.'
    return
  }
  if (!password.value) {
    error.value = '비밀번호를 입력해주세요.'
    return
  }
  callLoginApi();
}

const callLoginApi = () => {
  const input = {
    userId: id.value,
    password: password.value
  }

  login(input).then((res) => {
    console.log(res);
    if (res && res.status === 200) {
      const user = res.data;
      
      addToast(`${user.name}님 환영합니다.`, 'success', 3000); // 성공 토스트 메시지
      error.value = '';
      userStore.login(user.name);
      router.push('/'); // 로그인 성공 후 대시보드로 이동
    } else {
      error.value = res.message || '아이디 또는 비밀번호가 잘못되었습니다.';
    }

  }).catch((err) => {
    error.value = '아이디 또는 비밀번호가 잘못되었습니다.'
    console.error(err);
  });

}

// 페이지 이동 함수들
const goSignup = () => router.push('/signup')
const goFindId = () => router.push('/find-id')
const goFindPassword = () => router.push('/find-password')
</script>

<style scoped>
.login-container {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 320px;
  margin: 100px auto;
  font-family: Arial, sans-serif;
  text-align: center;
}

h2 {
  margin-bottom: 1.5rem;
}

input {
  width: 100%;
  padding: 10px;
  margin-bottom: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.95rem;
}

.login-btn {
  width: 100%;
  padding: 10px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.login-btn:hover {
  background: #369f6e;
}

.error {
  color: red;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

/* 추가 버튼 스타일 */
.extra-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 0.75rem;
  gap: 0.5rem;
}

.link-btn {
  background: none;
  border: none;
  color: #42b983;
  cursor: pointer;
  font-size: 0.85rem;
  padding: 0;
}

.link-btn:hover {
  text-decoration: underline;
}

.divider {
  color: #aaa;
  font-size: 0.85rem;
}

.signup-section {
  margin-top: 1.5rem;
  font-size: 0.9rem;
}

.signup-btn {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  margin-left: 0.3rem;
  font-weight: bold;
}

.signup-btn:hover {
  text-decoration: underline;
}
</style>
