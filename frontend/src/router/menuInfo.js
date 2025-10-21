import { ref } from "vue";
// import { authorizedMenuList } from "@/api/authorizedMenu.js";

export const menuList = ref([]);

// export async function initializeMenuList(userId) {
//   try {
//     const response = await authorizedMenuList({ id: userId });
//     menuList.value = response.data;
//     return menuList.value;
//   } catch (error) {
//     console.error("메뉴 목록을 가져오는데 실패했습니다:", error);
//     menuList.value = [];
//   }
// }
