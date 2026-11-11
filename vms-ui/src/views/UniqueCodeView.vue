<template>
  <div class="unique-code">
    <h1>售货机 MAC 地址</h1>
    <p class="hint">管理设备硬件唯一码（仅一级公司可访问），可解绑错误绑定的 MAC。</p>

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
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="unbindSelected">解绑 MAC</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="innerCode" label="设备编号" width="130" />
      <el-table-column prop="uniqueCode" label="MAC / 唯一码" min-width="200" show-overflow-tooltip />
      <el-table-column prop="vmDesc" label="设备名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="nodeName" label="点位" min-width="140" show-overflow-tooltip />
    </el-table>

    <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="onPageChange" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface UniqueRow {
  vmId: number
  innerCode: string
  uniqueCode: string | null
  vmDesc: string | null
  nodeName?: string
}

const rows = ref<UniqueRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterType = ref('1')
const filterText = ref('')
const selectedRows = ref<UniqueRow[]>([])

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterText.value) {
      params.set('searchType', filterType.value)
      params.set('searchValue', filterText.value)
    }
    const res = await api.get<{ total: number; data: UniqueRow[] }>(`/vms/api/uniqueCode/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e: any) {
    if (e?.type === 'error') {
      ElMessage.error(e.message || '无权访问')
    } else {
      ElMessage.error('加载设备 MAC 失败')
    }
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: UniqueRow[]) {
  selectedRows.value = selection
}

async function unbindSelected() {
  if (!selectedRows.value.length) return
  try {
    await ElMessageBox.confirm(`确定解绑选中的 ${selectedRows.value.length} 台设备的 MAC？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/uniqueCode/unbind', {
      ids: selectedRows.value.map((r) => r.vmId).join(','),
    }, { form: true })
    if (res?.type === 'success' || res?.code === 0) {
      ElMessage.success('解绑成功')
      loadList()
    } else {
      ElMessage.error(res?.message || '解绑失败')
    }
  } catch (e) {
    ElMessage.error('解绑失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.unique-code h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
