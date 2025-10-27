<template>
  <div class="layout-wrapper">
    <!-- Header -->
    <header class="header">
      <div class="header-inner">
        <div class="header-right">
          <!-- 로그인 버튼 -->
          <template v-if="!user.isLoggedIn">
            <RouterLink to="/login" class="btn btn-secondary" @click="clickLoginBtn">
              <i class="icon-sign-in"></i> 로그인
            </RouterLink>
          </template>

          <!-- 사용자 메뉴 -->
          <template v-else>
            <button class="btn btn-secondary" @click="toggleDropdownMenu">
              {{ `환영합니다. ${user.name}님!` }}
              <i :class="isOpen ? 'icon-chevron-up' : 'icon-chevron-down'"></i>
            </button>

            <!-- 사용자 메뉴 팝오버 -->
            <div v-if="isOpen" class="popover">
              <ul class="popover-list">
                <li>
                  <button class="popover-btn" @click="goMypage">
                    <i class="icon-user"></i> 내 정보
                  </button>
                </li>
                <li>
                  <button class="popover-btn" @click="goLogout">
                    <i class="icon-sign-out"></i> 로그아웃
                  </button>
                </li>
              </ul>
            </div>
          </template>
        </div>
      </div>
    </header>

    <!-- Sidebar -->
    <aside class="sidebar">
      <nav>
        <ul>
          <li><a href="#">Dashboard</a></li>
          <li><a href="#">Projects</a></li>
          <li><a href="#">Skills</a></li>
          <li><a href="#">Contact</a></li>
        </ul>
      </nav>
    </aside>

  <!-- Main -->
  <main v-if="user.isLoggedIn" class="main-with-sidebar">
    <div class="resume-title">
      <h1>유혜림의 이력서</h1>
    </div>

    <div class="page-wrapper">
      <!-- Dashboard Cards -->
      <section class="dashboard-cards">
        <div class="card">프로젝트 수<br>{{ stats.projects }}</div>
        <div class="card">기술 스택<br>{{ stats.skills }}</div>
        <div class="card">방문자<br>{{ stats.visitors }}</div>
      </section>

      <!-- Projects Table -->
      <section class="projects-table">
        <h2>프로젝트 목록</h2>
        <table>
          <thead>
            <tr>
              <th>프로젝트명</th>
              <th>Front-end</th>
              <th>Back-end</th>
              <th>투입기간</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="project in projects" :key="project.name">
              <td>{{ project.name }}</td>
              <td>{{ project.frontend }}</td>
              <td>{{ project.backend }}</td>
              <td>{{ project.period }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <!-- Skills -->
      <section class="skills-dashboard">
        <h2>기술 스택</h2>
        <div class="skill-bar" v-for="skill in skills" :key="skill.name">
          <span>{{ skill.name }}</span>
          <div class="bar">
            <div class="fill" :style="{ width: skill.level + '%' }"></div>
          </div>
        </div>
      </section>
    </div>
  </main>

  <!-- 로그인 안 되어 있으면 안내 문구 -->
  <main v-else class="main-with-sidebar">
    <div class="page-wrapper" style="text-align:center; padding-top:50px;">
      <h2>로그인이 필요합니다.</h2>
      <RouterLink to="/login" class="btn btn-primary">로그인 페이지로 이동</RouterLink>
    </div>
  </main>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";
import { logout } from "@/api/login.js";
import { addToast } from "@/utils/toastBus.js";

const router = useRouter();
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

function goMypage() {
  // router.push("/mypage");
  // isOpen.value = false;
}

async function goLogout() {
  try {
    isOpen.value = false;
    const input = { managerId: user.value.id };
    await logout(input);
    router.replace("/");
    userStore.reset();
    addToast(`로그아웃 완료되었습니다.`, 'success', 3000); 
  } catch {
    router.replace("/");
    userStore.reset();
  }
}

// 대시보드용 더미 데이터
const stats = ref({ projects: 7, skills: 8, visitors: 9999 });
const projects = ref([
  { name: "우리투자증권: 모니터링 서비스", frontend: "grafana, html, javascript", backend: "linux, prometheus, redis", period: "2025.10 ~ 진행중"},
  { name: "삼성선물: 어드민, 개발자센터 OPEN API 화면 및 서비스 구현", frontend: "Vue.js(quasar)", backend: "Spring Boot, hazelcast", period: "2025.04 ~ 2025.10"},
  { name: "사내 솔루션: 어드민 서비스, 개발자센터 OPEN API 구현", frontend: "Vue.js", backend: "Spring Boot", period: "2024.08 ~ 2025.04"},
  { name: "사내 토큰 발행 시스템, 사용자 이용 포탈 웹 서비스 구현", frontend: "Vue.js", backend: "Spring Boot", period: "2023.08 ~ 2024.07"},
  { name: "KB증권: 간편인증 웹 모듈 구현", frontend: "javascript", period: "2023.05 ~ 2023.07"},
  { name: "KB증권: 직원용 토큰 발행 웹 페이지 및 API 구현", frontend: "Vue.js", backend: "Spring Boot", period: "2022.07 ~ 2023.05"}, 
  { name: "카카오페이증권: 직원용 웹 페이지 및 어드민 페이지 구현", frontend: "Vue.js", period: "2021.01 ~ 2022.07"}, 
]);
const skills = ref([
  { name: "Vue.js", level: 60 },
  { name: "Spring Boot", level: 65 },
  { name: "JAVA", level: 65 },
  { name: "Javascript", level: 65 },
  { name: "MySQL", level: 60 },
  { name: "Oracle", level: 60 },
  { name: "Linux", level: 50 },
  { name: "Git", level: 60 }
]);

onMounted(async () => {
  await nextTick();
});
</script>

<style scoped lang="scss">
/* Header */
.layout-wrapper {
  display: flex;
  min-height: 100vh;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  border-bottom: 1px solid #ddd;
  padding: 0.75rem 1rem;
  background-color: #fff;
  z-index: 1000;
}

.header-inner {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 1rem;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
  position: relative;
}
/* Popover (사용자 메뉴) */
.popover {
  position: absolute;
  top: 100%; /* 버튼 바로 아래 */
  right: 0;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 0.5rem;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  padding: 0.5rem 0;
  z-index: 1001;
  min-width: 150px;
}

.popover-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.popover-list li {
  margin: 0;
}

.popover-btn {
  width: 100%;
  padding: 0.5rem 1rem;
  background: none;
  border: none;
  text-align: left;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-size: 0.95rem;
  color: #333;
}

.popover-btn:hover {
  background-color: #f0f0f0;
}

/* Sidebar */
.sidebar {
  position: fixed;
  top: 60px; /* header 높이 */
  left: 0;
  width: 200px;
  bottom: 0;
  background-color: #2c3e50;
  color: #fff;
  padding: 1rem;
}

.sidebar nav ul {
  list-style: none;
  padding: 0;
}

.sidebar nav ul li {
  margin-bottom: 1rem;
}

.sidebar nav ul li a {
  color: #fff;
  text-decoration: none;
}

.sidebar nav ul li a:hover {
  text-decoration: underline;
}

/* Main */
.main-with-sidebar {
  margin-left: 200px;
  padding: 70px 50px 50px 50px; /* header + padding */
  flex: 1;
}

/* Cards */
.dashboard-cards {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.dashboard-cards .card {
  flex: 1;
  background: #fff;
  padding: 1.5rem;
  border-radius: 0.5rem;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  text-align: center;
  font-weight: bold;
}

/* Projects Table */
.projects-table {
  margin-bottom: 2rem;
}

.projects-table table {
  width: 100%;
  border-collapse: collapse;
}

.projects-table th, .projects-table td {
  border: 1px solid #ddd;
  padding: 0.5rem;
  text-align: center;
}

.projects-table th {
  background-color: #f5f5f5;
}

/* Skills */
.skills-dashboard .skill-bar {
  margin-bottom: 1rem;
}

.skills-dashboard .skill-bar span {
  display: block;
  margin-bottom: 0.25rem;
}

.skills-dashboard .skill-bar .bar {
  width: 100%;
  height: 12px;
  background: #eee;
  border-radius: 6px;
  overflow: hidden;
}

.skills-dashboard .skill-bar .fill {
  height: 100%;
  background-color: #3498db;
  border-radius: 6px 0 0 6px;
}

.resume-title {
  margin-bottom: 1.5rem;
  text-align: center;
  h1 {
    font-size: 2rem;
    font-weight: bold;
    color: #333;
  }
}
</style>
