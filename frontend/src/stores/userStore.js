import { defineStore } from 'pinia'
export const useUserStore = defineStore('user', {
  state: () => ({
    user: {
      name: '',
      isLoggedIn: false,
    },
  }),
  actions: {
    login(name) {
      this.user.name = name;
      this.user.isLoggedIn = true;
    },
    reset() {
      this.user = { name: '', isLoggedIn: false };
    },
  },
});

