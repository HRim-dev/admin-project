<template>
  <div class="signup-container">
    <form @submit.prevent="onSubmit" @reset.prevent="onReset" ref="formRef">
      <h2>회원가입</h2>

      <div class="form-group">
        <label>아이디(이메일)</label>
        <div class="input-row">
          <input
            type="email"
            v-model="form.userId"
            placeholder="아이디(이메일)을 입력해주세요."
            @input="resetCheck"
          />
          <button
            type="button"
            class="small-btn"
            @click="clickDoubleCheck(form.userId)"
            v-if="!completeDoubleCheck"
          >
            중복확인
          </button>
          <span v-else class="check-icon">✔</span>
        </div>
      </div>

      <div class="form-group">
        <label>비밀번호</label>
        <div class="input-row">
          <input
            type="password"
            v-model="form.password"
            placeholder="비밀번호를 입력해주세요"
          />
        </div>
      </div>

      <div class="form-group">
        <label>비밀번호 확인</label>
        <div class="input-row">
          <input
            type="password"
            v-model="form.confirmPassword"
            placeholder="비밀번호를 한번 더 입력해주세요"
          />
 
        </div>
        <div v-if="form.confirmPassword">
          <small :class="passwordMatch ? 'text-positive' : 'text-negative'">
            {{ passwordMatch ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.' }}
          </small>
        </div>
      </div>

      <div class="form-group">
        <label>성명</label>
        <input
          type="text"
          v-model="form.name"
          placeholder="이름을 입력해주세요."
        />
      </div>

      <div class="form-group">
        <label>연락처</label>
        <input
          type="tel"
          v-model="form.phoneNumber"
          placeholder="000-0000-0000"
        />
      </div>

      <div class="actions">
        <button type="reset" class="secondary-btn">초기화</button>
        <button type="submit" class="primary-btn">회원가입</button>
      </div>

      <div class="footer">
        <span>이미 계정이 있으신가요?</span>
        <button type="button" class="link-btn" @click="goLogin">로그인</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import _ from 'lodash'
import { getDeptOptions, doubleCheckId, signUpUser } from '@/api/signUp'

const router = useRouter()

const formRef = ref(null)
const isPwd = ref(true)
const isPwdConfirm = ref(true)
const completeDoubleCheck = ref(false)
const isLoading = ref(false)

const form = ref({
  userId: '',
  password: '',
  confirmPassword: '',
  name: '',
  phoneNumber: ''
})

// ✅ 비밀번호 확인
const passwordMatch = computed(() => form.value.password === form.value.confirmPassword)

// ✅ 비밀번호 규칙
const passwordRule = (val) => {
  const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/
  return pattern.test(val)
}

// ✅ 중복확인
const clickDoubleCheck = (id) => {
  if (!id) return alert("이메일을 입력해주세요.")
  if (!/.+@.+\..+/.test(id)) return alert("유효하지 않은 이메일 형식입니다.")
  const queryData = { userId: id }

  doubleCheckId({ params: queryData }).then((res) => {
    if (res.data === true) {
      alert("이미 존재하는 이메일입니다.")
      completeDoubleCheck.value = false
      return
    }

    isLoading.value = true
    setTimeout(() => {
      completeDoubleCheck.value = true
      isLoading.value = false
    }, 800)
  })
}

const resetCheck = () => {
  completeDoubleCheck.value = false
}

// ✅ 회원가입
const onSubmit = () => {
  if (
    !form.value.userId ||
    !/.+@.+\..+/.test(form.value.userId) ||
    !form.value.password ||
    !passwordRule(form.value.password) ||
    !passwordMatch.value ||
    !form.value.name
  ) {
    alert("모든 필수 항목을 올바르게 입력해주세요.")
    return
  }

  if (!completeDoubleCheck.value) {
    alert("아이디 중복확인을 완료해주세요.")
    return
  }

  if (!confirm("회원가입 하시겠습니까?")) return

  form.value.phoneNumber = form.value.phoneNumber.replace(/-/g, '')
  signUpUser(form.value).then(() => {
    addToast(`회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.`, 'success', 3000); 
    router.push('/login')
  })
}

const onReset = () => {
  form.value = {
    userId: '',
    password: '',
    confirmPassword: '',
    name: '',
    phoneNumber: ''
  }
  completeDoubleCheck.value = false
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.signup-container {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 360px;
  margin: 100px auto;
  font-family: Arial, sans-serif;
  text-align: center;
}

h2 {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.2rem;
  text-align: left;
}

label {
  display: block;
  margin-bottom: 0.3rem;
  font-size: 0.9rem;
  color: #333;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.95rem;
}

.input-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.small-btn {
  padding: 8px 10px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.8rem;
  cursor: pointer;
}
.small-btn:hover {
  background: #369f6e;
}

.icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
}

.check-icon {
  font-size: 1.2rem;
  color: green;
}

.actions {
  margin-top: 1.5rem;
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.primary-btn {
  flex: 1;
  padding: 10px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.primary-btn:hover {
  background: #369f6e;
}

.secondary-btn {
  flex: 1;
  padding: 10px;
  background: #ccc;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.secondary-btn:hover {
  background: #bbb;
}

.footer {
  margin-top: 1.5rem;
  font-size: 0.9rem;
}

.link-btn {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  font-weight: bold;
  margin-left: 0.3rem;
}
.link-btn:hover {
  text-decoration: underline;
}

.text-positive {
  color: green;
  font-size: 0.8rem;
}
.text-negative {
  color: red;
  font-size: 0.8rem;
}
</style>
