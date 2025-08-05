import { createApp, ref } from 'vue'
import NotificationToast from '@/components/notification/NotificationToast.vue'

const notifications = ref([])

export function useNotification() {
  
  function showNotification(options) {
    const {
      type = 'info',
      title = '',
      message = '',
      duration = 3000,
      closable = true,
      persistent = false
    } = options

    // 컨테이너 div 생성
    const container = document.createElement('div')
    document.body.appendChild(container)

    // Vue 앱 생성
    const app = createApp(NotificationToast, {
      type,
      title,
      message,
      duration,
      closable,
      persistent,
      onClose: () => {
        app.unmount()
        document.body.removeChild(container)
      }
    })

    app.mount(container)
    
    return app
  }

  // 편의 메서드들
  function showSuccess(message, title = '', options = {}) {
    return showNotification({
      type: 'success',
      title,
      message,
      ...options
    })
  }

  function showError(message, title = '', options = {}) {
    return showNotification({
      type: 'error',
      title,
      message,
      ...options
    })
  }

  function showWarning(message, title = '', options = {}) {
    return showNotification({
      type: 'warning',
      title,
      message,
      ...options
    })
  }

  function showInfo(message, title = '', options = {}) {
    return showNotification({
      type: 'info',
      title,
      message,
      ...options
    })
  }

  return {
    showNotification,
    showSuccess,
    showError,
    showWarning,
    showInfo
  }
}