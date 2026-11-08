<template>
  <div class="vm-status-his">
    <h1>售货机历史状态</h1>
    <p class="hint">查询设备状态上报历史（默认最近 1 小时）。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="时间">
        <el-date-picker
          v-model="timeRange"
          type="datetimerange"
          value-format="yyyy-MM-dd HH:mm"
          start-placeholder="开始"
          end-placeholder="结束"
          style="width: 340px"
        />
      </el-form-item>
      <el-form-item label="筛选">
        <el-select v-model="filterType" style="width: 110px">
          <el-option label="设备编号" value="1" />
          <el-option label="点位" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="filterText" placeholder="关键字" clearable style="width: 150px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="innerCode" label="设备编号" width="120" />
      <el-table-column prop="nodeName" label="点位" min-width="130" show-overflow-tooltip />
      <el-table-column label="主板" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.vmStatus === 1 ? 'success' : 'warning'" size="small">
            {{ scope.row.vmStatus === 1 ? '正常' : '异常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="柜门" width="80">
        <template #default="scope">{{ scope.row.doorStatus === 1 ? '开' : '关' }}</template>
      </el-table-column>
      <el-table-column prop="tempValue" label="温度" width="80" />
      <el-table-column prop="vmStatusDetail" label="状态详情" min-width="160" show-overflow-tooltip />
      <el-table-column label="上报时间" min-width="150">
        <template #default="scope">{{ formatTime(scope.row.vmTime) }}</template>
      </el-table-column>
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

interface HisRow {
  vmId: number
  innerCode?: string
  nodeName?: string
  vmStatus: number | null
  doorStatus: number | null
  tempValue: string | null
  vmStatusDetail: string | null
  vmTime: string | null
}

const rows = ref<HisRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterType = ref('1')
const filterText = ref('')
const timeRange = ref<string[]>([])

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
    if (filterText.value) {
      params.set('selectType', filterType.value)
      params.set('selectValue', filterText.value)
    }
    if (timeRange.value && timeRange.value.length === 2) {
      params.set('startTime', timeRange.value[0])
      params.set('endTime', timeRange.value[1])
    }
    const res = await api.get<{ total: number; data: HisRow[] }>(`/vms/api/vmStatusHis/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载历史状态失败')
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
.vm-status-his h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
