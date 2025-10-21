<template>
  <div class="container">
    <form @submit.prevent="onSubmit" @reset.prevent="onReset" ref="formRef">
      <h2>회원가입</h2>

      <div class="form-group">
        <label>아이디(이메일)</label>
        <input
          type="email"
          v-model="form.userId"
          placeholder="아이디(이메일)을 입력해주세요."
          @input="resetCheck"
        />
        <button type="button" @click="clickDoubleCheck(form.userId)" v-if="!completeDoubleCheck">
          중복확인
        </button>
        <span v-else class="check-icon">✔</span>
      </div>

      <div class="form-group">
        <label>비밀번호</label>
        <input
          :type="isPwd ? 'password' : 'text'"
          v-model="form.password"
          placeholder="비밀번호를 입력해주세요"
        />
        <button type="button" @click="isPwd = !isPwd">{{ isPwd ? '👁️' : '🙈' }}</button>
      </div>

      <div class="form-group">
        <label>비밀번호 확인</label>
        <input
          :type="isPwdConfirm ? 'password' : 'text'"
          v-model="form.confirmPassword"
          placeholder="비밀번호를 한번 더 입력해주세요"
        />
        <button type="button" @click="isPwdConfirm = !isPwdConfirm">{{ isPwdConfirm ? '👁️' : '🙈' }}</button>
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
      <div class="form-group actions">
        <button type="reset">초기화</button>
        <button type="submit">회원가입</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import _ from 'lodash';
import { getDeptOptions, doubleCheckId, signUpUser } from '@/api/signUp';

const router = useRouter();

const formRef = ref(null);
const isPwd = ref(true);
const isPwdConfirm = ref(true);
const completeDoubleCheck = ref(false);
const isLoading = ref(false);

const form = ref({
  userId: '',
  password: '',
  confirmPassword: '',
  name: '',
});

let deptOptions = [];
const filterDeptOptions = ref([]);

// 패스워드 유효성 검사
const passwordRule = (val) => {
  const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
  return passwordPattern.test(val);
};

const passwordMatch = computed(() => form.value.password === form.value.confirmPassword);

const callGetDeptNmOptions = () => {
  getDeptOptions().then(res => {
    deptOptions = _.cloneDeep(res.data.result);
  });
};

const filterDept = () => {
  const val = form.value.managerDeptName.toLowerCase();
  if (!val) {
    filterDeptOptions.value = deptOptions;
    return;
  }

  filterDeptOptions.value = deptOptions.filter(opt => opt.toLowerCase().includes(val));
};

const clickDoubleCheck = (id) => {
  if (!id) {
    alert("TR ID를 입력해주세요.");
    return;
  }

  if (!/.+@.+\..+/.test(id)) {
    alert("유효하지 않은 이메일 형식입니다.");
    return;
  }

  const queryData = { userId: id };
  doubleCheckId({params:queryData}).then(res => {
    console.log(res)
    if (res.data == true) {
      alert("이미 존재하는 ID(이메일)입니다.");
      completeDoubleCheck.value = false;
      return;
    }

    isLoading.value = true;
    setTimeout(() => {
      completeDoubleCheck.value = true;
      isLoading.value = false;
    }, 1000);
  });
};

const resetCheck = () => {
  completeDoubleCheck.value = false;
};

const onSubmit = () => {
  if (
    !form.value.userId ||
    !/.+@.+\..+/.test(form.value.userId) ||
    !form.value.password ||
    !passwordRule(form.value.password) ||
    !form.value.confirmPassword ||
    !passwordMatch.value ||
    !form.value.name 
  ) {
    alert("모든 필수 입력 값을 정확히 입력해주세요.");
    return;
  }

  if (!completeDoubleCheck.value) {
    alert("아이디 중복확인을 완료해주세요.");
    return;
  }

  if (!confirm("회원가입 하시겠습니까?")) return;

  form.value.phoneNumber = form.value.phoneNumber.replace(/-/g, '');
  callSignupUser(form.value);
};

const callSignupUser = (input) => {
  signUpUser(input).then((res) => {

    console.log(res)
    // alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
    // router.push("/login");
    // router.push("/login");
  });
};

const onReset = () => {
  form.value = {
    userId: '',
    password: '',
    confirmPassword: '',
    name: '',
  };
  completeDoubleCheck.value = false;
};

onMounted(() => {
  // callGetDeptNmOptions();
});
</script>

<style scoped>
.container {
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

form h2 {
  text-align: center;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
  display: flex;
  flex-direction: column;
}

input, select, button {
  padding: 0.5rem;
  font-size: 1rem;
}

.actions {
  display: flex;
  justify-content: space-between;
}

.check-icon {
  font-size: 1.5rem;
  color: green;
  margin-left: 1rem;
}

.text-positive {
  color: green;
}
.text-negative {
  color: red;
}
</style>
