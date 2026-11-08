<template>
  <div class="vm-status">
    <h1>售货机状态</h1>
    <p class="hint">实时监控设备网络/主板/派单/温度状态，可派单或取消派单。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="筛选">
        <el-select v-model="filterType" style="width: 110px">
          <el-option label="设备编号" value="1" />
          <el-option label="点位" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="filterText" placeholder="关键字" clearable style="width: 150px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item label="网络">
        <el-select v-model="filterNet" clearable placeholder="全部" style="width: 90px">
          <el-option label="在线" :value="1" />
          <el-option label="离线" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="派单">
        <el-select v-model="filterTask" clearable placeholder="全部" style="width: 90px">
          <el-option label="已派单" :value="1" />
          <el-option label="未派单" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
        <el-button type="success" :disabled="!selectedRows.length" @click="dispatch(false)">派单</el-button>
        <el-button :disabled="!selectedRows.length" @click="dispatch(true)">取消派单</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="innerCode" label="设备编号" width="120" />
      <el-table-column prop="nodeName" label="点位" min-width="130" show-overflow-tooltip />
      <el-table-column label="网络" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.netStatus === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.netStatus === 1 ? '在线' : '离线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="主板" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.vmcStatus === 1 ? 'success' : 'warning'" size="small">
            {{ scope.row.vmcStatus === 1 ? '正常' : '异常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="派单" width="80">
        <template #default="scope">{{ scope.row.taskStatus === 1 ? '已派单' : '未派单' }}</template>
      </el-table-column>
      <el-table-column prop="tempValue" label="温度" width="80" />
      <el-table-column prop="vmStatusDetail" label="状态详情" min-width="150" show-overflow-tooltip />
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

interface StatusRow {
  vmId: number
  innerCode: string
  nodeName?: string
  netStatus: number | null
  vmcStatus: number | null
  taskStatus: number | null
  tempValue: string | null
  vmStatusDetail: string | null
  vmTime: string | null
}

const rows = ref<StatusRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterType = ref('1')
const filterText = ref('')
const filterNet = ref<number | undefined>(undefined)
const filterTask = ref<number | undefined>(undefined)
const selectedRows = ref<StatusRow[]>([])

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
    if (filterNet.value !== undefined) params.set('netStatus', String(filterNet.value))
    if (filterTask.value !== undefined) params.set('taskStatus', String(filterTask.value))
    const res = await api.get<{ total: number; data: StatusRow[] }>(`/vms/api/vmStatus/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载设备状态失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: StatusRow[]) {
  selectedRows.value = selection
}

async function dispatch(cancel: boolean) {
  if (!selectedRows.value.length) return
  try {
    const res = await api.post<any>('/vms/api/vmStatus/dispatch', {
      ids: selectedRows.value.map((r) => r.vmId).join(','),
      cancel: String(cancel),
    }, { form: true })
    if (res?.type === 'success') {
      ElMessage.success(res.message)
      loadList()
    } else {
      ElMessage.error(res?.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.vm-status h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
