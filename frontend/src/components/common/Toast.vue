<template>
  <div class="toast-container">
    <transition-group name="toast-fade" tag="div">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="toast"
        :class="toast.type"
        @click="removeToast(toast.id)"
      >
        {{ toast.message }}
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { toastBus } from "@/utils/toastBus.js"; // 전역 이벤트 버스 사용

const toasts = ref([]);

const addToast = (message, type = "info", duration = 3000) => {
  const id = Date.now();
  toasts.value.push({ id, message, type });

  setTimeout(() => removeToast(id), duration);
};

const removeToast = (id) => {
  toasts.value = toasts.value.filter((t) => t.id !== id);
};

// ✅ 이벤트 버스에서 토스트 추가 이벤트 수신
toastBus.on("add", (payload) => {
  addToast(payload.message, payload.type, payload.duration);
});
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 1rem;
  right: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  z-index: 9999;
}

.toast {
  min-width: 220px;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  color: #fff;
  font-size: 0.9rem;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  opacity: 0.95;
  cursor: pointer;
}

.toast.info { background-color: #3498db; }
.toast.success { background-color: #2ecc71; }
.toast.error { background-color: #e74c3c; }

.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: all 0.3s ease;
}
.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
