import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api, ApiError } from '../api/client'

interface UserInfo {
  userId: number
  userName: string
  userAccount: string
  company?: {
    id: number
    name: string
    level: number
  }
  roles: string[]
  permissions: string[]
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const sessionId = ref('')

  const isLogin = computed(() => userInfo.value !== null)
  const userName = computed(() => userInfo.value?.userName || '')
  const userAccount = computed(() => userInfo.value?.userAccount || '')

  /**
   * Check session status by calling /auth/me endpoint.
   * Used on app initialization to restore session after page refresh.
   */
  async function checkSession() {
    try {
      const data = await api.get<UserInfo & { authenticated: boolean; message?: string }>(
        '/vms/api/auth/me',
      )

      if (data.authenticated && data.userId) {
        userInfo.value = {
          userId: data.userId,
          userName: data.userName,
          userAccount: data.userAccount,
          company: data.company,
          roles: data.roles || [],
          permissions: data.permissions || [],
        }
        sessionId.value = `session-${data.userAccount}-${Date.now()}`
        return true
      } else {
        userInfo.value = null
        sessionId.value = ''
        return false
      }
    } catch (error) {
      // 只有 401/302 才算未登录，其他错误（网络、服务器）不清除状态
      if (error instanceof ApiError && (error.type === 'unauthorized')) {
        userInfo.value = null
        sessionId.value = ''
      }
      return false
    }
  }

  async function login(account: string, password: string, captcha: string) {
    const data = await api.post<{ code: number; message: string; href?: string }>(
      '/submitLogin',
      { userName: account, password, validCode: captcha },
      { form: true },
    )

    if (data.code === 1) {
      // After successful login, fetch full user info
      await checkSession()
    }

    return data
  }

  async function logout() {
    try {
      await api.post('/vms/api/auth/logout', {})
    } catch (error) {
      // Ignore logout errors
    } finally {
      userInfo.value = null
      sessionId.value = ''
    }
  }

  /**
   * Check if user has a specific permission.
   */
  function hasPermission(permission: string): boolean {
    return userInfo.value?.permissions.includes(permission) || false
  }

  /**
   * Check if user has a specific role.
   */
  function hasRole(role: string): boolean {
    return userInfo.value?.roles.includes(role) || false
  }

  return {
    userInfo,
    sessionId,
    isLogin,
    userName,
    userAccount,
    checkSession,
    login,
    logout,
    hasPermission,
    hasRole,
  }
})
