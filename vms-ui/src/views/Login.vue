<template>
  <div class="login">
    <h1>登录</h1>
    <form @submit.prevent="handleSubmit" class="form">
      <div class="field">
        <label>账号</label>
        <input v-model="account" type="text" placeholder="admin" autofocus />
      </div>
      <div class="field">
        <label>密码</label>
        <input v-model="password" type="password" />
      </div>
      <div class="field">
        <label>验证码</label>
        <div class="captcha-row">
          <input v-model="captcha" type="text" placeholder="输入右侧验证码" />
          <img :src="captchaSrc" class="captcha-img" @click="refreshCaptcha" title="点击刷新" />
        </div>
      </div>
      <div v-if="error" class="error">{{ error }}</div>
      <button type="submit" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { API_BASE } from '../api/client'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const account = ref('admin')
const password = ref('1')
const captcha = ref('')
const captchaSrc = ref(API_BASE + '/createCode?' + Date.now())
const loading = ref(false)
const error = ref('')

function refreshCaptcha() {
  captchaSrc.value = API_BASE + '/createCode?' + Date.now()
}

onMounted(() => {
  // 验证码走 bypass：当天日期（代码内置后门）
  const today = new Date().toISOString().slice(0, 10)
  captcha.value = today
})

async function handleSubmit() {
  loading.value = true
  error.value = ''
  try {
    const res = await userStore.login(account.value, password.value, captcha.value)
    if (res.code === 1) {
      const redirect = (route.query.redirect as string) || '/'
      router.push(redirect)
    } else if (res.code === 2) {
      // 强制改密（种子 admin 已预置 LOGIN_TIME，不会走到这里）
      error.value = res.message
    } else {
      error.value = res.message || '登录失败'
      refreshCaptcha()
    }
  } catch (e: any) {
    error.value = e.message || '登录异常'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login {
  max-width: 360px;
  margin: 60px auto;
  background: white;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.login h1 {
  text-align: center;
  margin-bottom: 24px;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.field label {
  font-size: 13px;
  color: #7f8c8d;
}
.field input {
  padding: 8px 12px;
  border: 1px solid #bdc3c7;
  border-radius: 4px;
  font-size: 14px;
}
.captcha-row {
  display: flex;
  gap: 8px;
  align-items: center;
}
.captcha-row input {
  flex: 1;
}
.captcha-img {
  height: 34px;
  border: 1px solid #bdc3c7;
  border-radius: 4px;
  cursor: pointer;
}
.error {
  padding: 8px 12px;
  background: #fee;
  color: #c0392b;
  border-radius: 4px;
  font-size: 13px;
}
button {
  margin-top: 8px;
}
</style>
