import mitt from "mitt";

export const toastBus = mitt();

/**
 * 전역에서 호출 가능한 토스트 함수
 * @param {string} message - 표시할 메시지
 * @param {string} type - 'info' | 'success' | 'error'
 * @param {number} duration - 표시 시간 (ms)
 */
export const addToast = (message, type = "info", duration = 3000) => {
  toastBus.emit("add", { message, type, duration });
};
