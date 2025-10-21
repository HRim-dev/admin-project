import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  // ✅ 1) 상태 (state)
  state: () => ({
    name: '',
    isLoggedIn: false,
  }),

  // ✅ 2) 계산된 속성 (getters)
  getters: {
    welcomeMessage: (state) => {
      return state.isLoggedIn
        ? `안녕하세요, ${state.name}님!`
        : '로그인이 필요합니다.'
    },
  },

  // ✅ 3) 액션 (actions)
  actions: {
    login(name) {
      this.name = name
      this.isLoggedIn = true
    },
    logout() {
      this.name = ''
      this.isLoggedIn = false
    },
  },
})
