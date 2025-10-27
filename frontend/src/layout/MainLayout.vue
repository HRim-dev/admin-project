<template>
  <div class="layout-wrapper">
    <!-- header -->
    <div
      v-if="isDev"
      class="fixed top-0 left-0 w-full bg-blue-600 text-white text-xs text-center z-[1003]"
    >
      ⚠ 현재는 개발 서버(Dev)입니다.
    </div>

    <header class="header--bordered">
      <div class="inner-container flex items-center justify-between">
        <div class="flex items-center gap-6">
          <!-- 로그인 버튼 -->
          <template v-if="!user.isLoggedIn">
            <button
              class="btn btn-text btn-secondary btn-large btn-rounded"
              @click="clickLoginBtn"
            >
              <RouterLink to="/login" @click="clickLoginBtn" class="btn-link">
                <i class="pi pi-sign-in"></i>
                로그인
              </RouterLink>
            </button>
          </template>

          <!-- 사용자 메뉴 -->
          <template v-else>
            <button
              aria-label="사용자 메뉴 열기"
              class="btn btn-text btn-secondary btn-large btn-rounded"
              :class="{ 'btn-secondary-active': isOpen }"
              @click="toggleDropdownMenu"
            >
              {{ `${user.name}(${user.deptName})님` }}
              <i
                class="text-muted !text-helper"
                :class="isOpen ? 'pi pi-chevron-up' : 'pi pi-chevron-down'"
              ></i>
            </button>

            <!-- 사용자 메뉴 팝오버 -->
            <div
              v-if="isOpen"
              class="popover absolute top-16 right-6 min-w-[190px]"
            >
              <ul class="space-y-1">
                <li>
                  <button
                    class="btn btn-text btn-secondary w-full flex justify-start gap-3"
                    @click="goMypage"
                  >
                    <i class="pi pi-user"></i> 내 정보
                  </button>
                </li>
                <li>
                  <button
                    class="btn btn-text btn-secondary w-full flex justify-start gap-3"
                    @click="goLogout"
                  >
                    <i class="pi pi-sign-out"></i> 로그아웃
                  </button>
                </li>
              </ul>
            </div>
          </template>
        </div>
      </div>
    </header>

    <!-- main -->
    <main
      :class="[ 
        'layout-admin', 
        user.isLoggedIn && leftDrawerOpen
          ? 'layout-admin--with-sidebar'
          : 'layout-admin--full-width' 
      ]"
    >
      <div class="page-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";
import { logout } from "@/api/login.js";

defineOptions({ name: "MainLayout" });

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const { user } = storeToRefs(userStore);

const leftDrawerOpen = ref(false);
const isOpen = ref(false);

function clickLoginBtn() {
  leftDrawerOpen.value = false;
}

function toggleDropdownMenu() {
  isOpen.value = !isOpen.value;
}

function goHome() {
  router.push("/");
}

function goMypage() {
  router.push("/mypage");
  isOpen.value = false;
}

async function goLogout() {
  try {
    isOpen.value = false;
    const input = { managerId: user.value.id };
    await logout(input);
    router.replace("/");
    userStore.reset();
    alert("로그아웃 성공");
  } catch {
    router.replace("/");
    userStore.reset();
  }
}

onMounted(async () => {
  await nextTick();
});

const isDev = location.hostname.includes("dev") || location.hostname === "localhost";
</script>

<style scoped lang="scss">
.layout-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.layout-admin {
  flex: 1;
}

/* ===== 버튼 스타일 ===== */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  outline: none;
  background: none;

  &.btn-large {
    font-size: 1rem;
    padding: 0.5rem 1rem;
  }

  &.btn-rounded {
    border-radius: 9999px;
  }

  &.btn-secondary {
    color: #333;
  }

  &.btn-text:hover {
    background-color: rgba(0, 0, 0, 0.05);
  }

  &.btn-secondary-active {
    background-color: #e0e7ff;
    color: #1e3a8a;
  }

  i {
    margin-right: 0.4rem;
  }

  .btn-link {
    display: flex;
    align-items: center;
    color: inherit;
    text-decoration: none;
  }
}

/* ===== 팝오버 스타일 ===== */
.popover {
  position: absolute;
  background-color: #fff;
  border-radius: 0.5rem;
  border: 1px solid #ddd;
  padding: 0.75rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.space-y-1 > * + * {
  margin-top: 0.25rem;
}
</style>
