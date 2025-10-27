<template>
  <Toast/>
  <router-view />
</template>
<script setup>
import Toast from "@/components/common/Toast.vue";
defineOptions({
  name: "App",
});
</script>
<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
