<template>
  <div class="login-container">
    <h2>로그인</h2>
    <div v-if="error" class="error">{{ error }}</div>
    <input v-model="id" type="text" placeholder="아이디" />
    <input v-model="password" type="password" placeholder="비밀번호" />
    <button @click="clickLoginBtn">로그인</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { login } from '@/api/login';
import { useRoute, useRouter } from "vue-router";
const route = useRoute();
const router = useRouter();

const id = ref('')
const password = ref('')
const error = ref('')

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
    const result = res.data;
    if (res && res.status === 200) {
      alert('로그인 성공!');
      error.value = '';
      router.push('/'); // 로그인 성공 후 대시보드로 이동
    } else {
      error.value = res.message || '아이디 또는 비밀번호가 잘못되었습니다.';
    }
    /*
    if (result && result.success) {
      alert('로그인 성공!');
      error.value = '';
      // 추가로 로그인 성공 후 처리할 로직을 여기에 작성하세요.
    } else {
      error.value = '아이디 또는 비밀번호가 잘못되었습니다.';
    }
    */
//   if (username.value === 'admin' && password.value === '1234') {
//     alert('로그인 성공!')
//     error.value = ''
//   } else {
//     error.value = '아이디 또는 비밀번호가 잘못되었습니다.'
//   }
  }).catch((err) => {
    error.value = '아이디 또는 비밀번호가 잘못되었습니다.'
    console.error(err);
  });

}
</script>

<style scoped>
.login-container {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 300px;
  margin: 100px auto;
  font-family: Arial, sans-serif;
}

h2 {
  margin-bottom: 1.5rem;
  text-align: center;
}

input {
  width: 100%;
  padding: 10px;
  margin-bottom: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background: #369f6e;
}

.error {
  color: red;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  text-align: center;
}
</style>
