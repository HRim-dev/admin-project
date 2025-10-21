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
        <div class="flex items-center gap-4">
          <PButton
            v-if="user.isLoggedIn"
            icon="pi pi-bars"
            aria-label="전체 메뉴 열기"
            @click="toggleLeftDrawer"
            severity="secondary"
            variant="outlined"
            size="large"
            class="!shadow-none"
          />

        </div>

        <div class="flex items-center gap-6">
          <PButton label="서버 상태" size="large" severity="secondary" rounded text @click="openServerStatus" />
          <PButton label="어드민 가이드" size="large" severity="secondary" rounded text @click="openAdminGuide" />
          <PButton label="포탈 가이드" size="large" severity="secondary" rounded text @click="openPortalGuide" />
          <PButton label="포탈 바로가기" size="large" severity="secondary" rounded text @click="handlePortalClick" />

          <template v-if="!user.isLoggedIn">
            <PButton
              asChild
              v-slot="slotProps"
              severity="secondary"
              variant="text"
              size="large"
              rounded
              @click="clickLoginBtn"
            >
              <RouterLink to="/login" @click="clickLoginBtn" :class="slotProps.class">
                <i class="pi pi-sign-in"></i>
                로그인
              </RouterLink>
            </PButton>
          </template>

          <template v-else>
            <PButton
              label="모니터링"
              severity="secondary"
              variant="text"
              size="large"
              rounded
              @click="handleClickMonitoring('/analysis/time')"
            />
            <PButton
              aria-label="사용자 메뉴 열기"
              severity="secondary"
              variant="text"
              size="large"
              rounded
              :class="isOpen ? 'btn-secondary-active' : ''"
              @click="toggleDropdownMenu"
            >
              {{ `${user.name}(${user.deptName})님` }}
              <i
                class="text-muted !text-helper"
                :class="isOpen ? 'pi pi-chevron-up' : 'pi pi-chevron-down'"
              ></i>
            </PButton>
            <PPopover
              ref="op"
              @show="isOpen = true"
              @hide="isOpen = false"
              appendTo="self"
              class="absolute !top-16 !right-6 !left-auto min-w-[190px]"
            >
              <ul class="space-y-1">
                <li>
                  <PButton
                    label="내 정보"
                    icon="pi pi-user"
                    @click="goMypage"
                    severity="secondary"
                    variant="text"
                    class="w-full flex !justify-start !gap-3"
                  />
                </li>
                <li>
                  <PButton
                    label="로그아웃"
                    icon="pi pi-sign-out"
                    @click="goLogout"
                    severity="secondary"
                    variant="text"
                    class="w-full flex !justify-start !gap-3"
                  />
                </li>
              </ul>
            </PPopover>
          </template>
        </div>
      </div>
    </header>

    <!-- side nav menu -->
    <aside
      v-if="user.isLoggedIn"
      :class="[
        'side-nav inner-container pb-6',
        leftDrawerOpen ? 'side-nav--visible' : 'side-nav--hidden',
      ]"
    >
      <nav class="space-y-4 mt-4" aria-label="전체 메뉴">
        <div class="w-full text-sm font-semibold text-muted text-left">
          ADMIN MENU
        </div>
        <PButton
          type="button"
          @click="handleClickHome"
          severity="secondary"
          variant="text"
          size="large"
          class="w-full !justify-start !font-semibold !gap-3"
          :class="route.path === '/' ? 'btn-secondary-active' : ''"
        >
          <House /><span class="font-semibold">Home</span>
        </PButton>

        <PPanelMenu
          v-model:expandedKeys="expandedKeys"
          :model="items"
          class="w-full space-y-4"
        >
          <template #item="{ item, props }">
            <div
              v-bind="props.action"
              @click="() => handleClick(item)"
              class="menu-item"
              :class="[item.items ? 'menu-item-parent' : 'menu-item-child', route.path === item.route ? 'btn-secondary-active' : '']"
            >
              <span
                v-if="!item.items"
                class="absolute -left-[1px] top-0 h-full w-[1px]"
                :class="[route.path === item.route ? 'bg-gray-600' : '']"
              ></span>
              <component
                v-if="item.iconComponent"
                :is="item.iconComponent"
                :size="20"
                class="flex-shrink-0"
              />
              <span class="flex-1">{{ item.label }}</span>
              <i
                v-if="item.items"
                class="pi ml-auto transition-transform duration-150 text-muted !text-helper"
                :class="expandedKeys[item.key] ? 'pi-chevron-down' : 'pi-chevron-right'"
              />
            </div>
          </template>
        </PPanelMenu>
      </nav>
    </aside>

    <!-- main -->
    <main
      :class="[
        'layout-admin',
        user.isLoggedIn && leftDrawerOpen
          ? 'layout-admin--with-sidebar'
          : 'layout-admin--full-width',
      ]"
    >
      <div class="page-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";
import { menuList } from "@/router/menuInfo";
// import { loadAuthorizedMenuList } from "../js/menu-update.js";
import { logout } from "@/api/login.js";
import {
  LayoutGrid,
  Lock,
  Database,
  Sliders,
  Package,
  GitBranch,
  AlertTriangle,
  FileCheck,
  Clock,
  UserSearch,
  House,
  FolderOpen,
} from "lucide-vue-next";
// import LogoImage from "src/components/logo/LogoImage.vue";

defineOptions({ name: "MainLayout" });

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const { user } = storeToRefs(userStore);

const menuItems = ref([]);
const leftDrawerOpen = ref(false);
const expandedKeys = ref({});
const op = ref();
const isOpen = ref(false);

const iconComponentMap = {
  SP000: LayoutGrid,
  PR000: Lock,
  TR000: Database,
  AP000: Sliders,
  PD000: Package,
  RM000: GitBranch,
  AC000: AlertTriangle,
  AR000: FileCheck,
  HS000: Clock,
  MI000: UserSearch,
};

function settingMenuList() {
  if (!menuList.value || !Array.isArray(menuList.value)) {
    menuItems.value = [];
    return;
  }
  menuItems.value = menuList.value.filter(
    (menu) => menu.subMenu && menu.menuId !== ""
  );
  leftDrawerOpen.value = true;
}

function convertToPanelMenu(list) {
  return list.map((menu, i) => ({
    key: `${i}`,
    label: (menu.title),
    iconComponent: iconComponentMap[menu.menuId] || FolderOpen,
    items:
      menu.subMenu?.map((sub, j) => ({
        key: `${i}_${j}`,
        label: (sub.title),
        route: sub.to,
      })) || [],
  }));
}

const items = computed(() => convertToPanelMenu(menuItems.value));

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}

function goHome() {
  router.push("/");
}

function goMypage() {
  router.push("/mypage");
  op.value?.hide();
}

async function goLogout() {
  try {
    op.value?.hide();
    isOpen.value = false;
    // await loadAuthorizedMenuList(undefined, router);
    menuList.value = [];
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

function toggleDropdownMenu(event) {
  isOpen.value ? op.value?.hide() : op.value?.show(event);
}

function handleClick(item) {
  if (item.route) router.push(item.route);
}

function handleClickMonitoring(to) {
  const routeData = router.resolve({ path: to });
  window.open(routeData.href, "_blank");
}

function handlePortalClick() {
  const url = isDev
    ? "https://openapidev.ssfutures.com:2080"
    : "https://openapi.ssfutures.com";
  window.open(url, "_blank");
}

const openServerStatus = () => window.open("/monitoring/index.html", "_blank");
const openAdminGuide = () => window.open("/docs/admin/index.html", "_blank");
const openPortalGuide = () => window.open("/docs/portal/index.html", "_blank");

function handleClickHome() {
  goHome();
  expandedKeys.value = {};
}

onMounted(async () => {
  await nextTick();
  // settingMenuList();
});

watch(
  () => user.value.isLoggedIn,
  (v) => {
    if (!v) {
      menuList.value = [];
      menuItems.value = [];
      expandedKeys.value = {};
      leftDrawerOpen.value = false;
    }
  }
);

// watch(menuList, () => settingMenuList(), { deep: true });

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
:deep(.p-popover) {
  border-radius: var(--radius-lg) !important;
  margin-block-start: 0 !important;
  .p-popover-content {
    padding: 0.75rem;
  }
}
::v-deep([data-pc-section="rootlist"]) {
  margin-top: 0.5rem;
  margin-left: 1.25rem;
  border-left: 1px solid var(--theme-border);
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}
</style>
