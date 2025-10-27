import { defineStore } from 'pinia'
export const useUserStore = defineStore('user', {
  state: () => ({
    user: {
      name: '',
      deptName: '',
      isLoggedIn: false,
    },
  }),
  actions: {
    login(name, deptName) {
      this.user.name = name;
      this.user.deptName = deptName;
      this.user.isLoggedIn = true;
    },
    reset() {
      this.user = { name: '', deptName: '', isLoggedIn: false };
    },
  },
});

