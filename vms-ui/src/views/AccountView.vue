<template>
  <div class="account-view">
    <h1>账号管理</h1>
    <p class="hint">管理个人账号（姓名/密码）。手机号修改依赖短信网关（旧 UcPaas 已停用），请联系管理员。</p>

    <el-card shadow="never" style="max-width: 560px">
      <el-descriptions :column="1" border size="small" style="margin-bottom: 16px">
        <el-descriptions-item label="账号">{{ me?.userAccount }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ me?.userName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ me?.userPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="公司">{{ me?.companyName || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">修改姓名</el-divider>
      <el-form :inline="true">
        <el-form-item><el-input v-model="nameForm.userName" placeholder="新姓名" maxlength="40" style="width: 180px" /></el-form-item>
        <el-form-item><el-button type="primary" :loading="savingName" @click="saveName">保存</el-button></el-form-item>
      </el-form>

      <el-divider content-position="left">修改密码</el-divider>
      <el-form :inline="true">
        <el-form-item><el-input v-model="pwdForm.pwd" type="password" placeholder="新密码" maxlength="40" show-password style="width: 180px" /></el-form-item>
        <el-form-item><el-button type="warning" :loading="savingPwd" @click="savePwd">保存</el-button></el-form-item>
      </el-form>
      <p class="hint">修改密码后下次登录生效。原短信验证码流程已随短信网关停用而失效。</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface AccountMe {
  id: number
  userAccount: string
  userName: string
  userPhone: string | null
  companyName?: string
}

const me = ref<AccountMe | null>(null)
const nameForm = ref({ userName: '' })
const pwdForm = ref({ pwd: '' })
const savingName = ref(false)
const savingPwd = ref(false)

async function loadMe() {
  try {
    me.value = await api.get<AccountMe>('/vms/api/account/me')
    nameForm.value.userName = me.value.userName || ''
  } catch (e) {
    ElMessage.error('加载账号信息失败')
  }
}

async function saveName() {
  if (!nameForm.value.userName) {
    ElMessage.warning('请填写姓名')
    return
  }
  savingName.value = true
  try {
    const res = await api.post<any>('/vms/api/account/updateUserName', { ...nameForm.value }, { form: true })
    if (res?.code === 0) {
      ElMessage.success('修改成功')
      loadMe()
    } else {
      ElMessage.error(res?.message || '修改失败')
    }
  } catch (e) {
    ElMessage.error('修改失败')
  } finally {
    savingName.value = false
  }
}

async function savePwd() {
  if (!pwdForm.value.pwd) {
    ElMessage.warning('请填写新密码')
    return
  }
  savingPwd.value = true
  try {
    const res = await api.post<any>('/vms/api/account/updatePassword', { ...pwdForm.value }, { form: true })
    if (res?.code === 0) {
      ElMessage.success(res.message || '修改成功，下次登录生效')
      pwdForm.value.pwd = ''
    } else {
      ElMessage.error(res?.message || '修改失败')
    }
  } catch (e) {
    ElMessage.error('修改失败')
  } finally {
    savingPwd.value = false
  }
}

onMounted(loadMe)
</script>

<style scoped>
.account-view h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
</style>
