<template>
  <div class="syslog-view">
    <h1>系统日志</h1>
    <p class="hint">查看公司范围内的操作日志（按操作人筛选）。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="操作人">
        <el-input v-model="filterUserName" placeholder="操作人姓名" clearable style="width: 180px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="logTime" label="时间" min-width="160">
        <template #default="scope">{{ formatTime(scope.row.logTime) }}</template>
      </el-table-column>
      <el-table-column prop="userName" label="操作人" width="120" />
      <el-table-column prop="logOperation" label="操作" width="80" />
      <el-table-column prop="logDesc" label="内容" min-width="320" show-overflow-tooltip />
    </el-table>

    <el-pagination
      class="pager"
      layout="total, prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page="page"
      @current-change="onPageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface LogRow {
  logDesc: string
  logOperation: string
  moduleId: number | null
  logTime: string | null
  userName: string | null
}

const rows = ref<LogRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterUserName = ref('')

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterUserName.value) params.set('userName', filterUserName.value)
    const res = await api.get<{ total: number; data: LogRow[] }>(`/vms/api/sysLog/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载日志失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

onMounted(loadList)
</script>

<style scoped>
.syslog-view h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
