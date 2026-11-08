<template>
  <div class="vm-stock">
    <h1>售货机库存</h1>
    <p class="hint">查看各设备货道库存与临期情况（点击设备查看明细）。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="筛选">
        <el-select v-model="filterType" style="width: 110px">
          <el-option label="设备编号" value="1" />
          <el-option label="点位" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="filterText" placeholder="关键字" clearable style="width: 160px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="innerCode" label="设备编号" width="130" />
      <el-table-column prop="vmDesc" label="设备名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="nodeName" label="点位" min-width="150" show-overflow-tooltip />
      <el-table-column label="库存状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.stockStatus === 2 ? 'danger' : 'success'" size="small">
            {{ scope.row.stockStatus === 2 ? '缺货' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openChannels(scope.row)">货道明细</el-button>
        </template>
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

    <el-dialog v-model="channelsVisible" :title="`货道库存：${currentVm?.innerCode || ''}`" width="720px">
      <el-table :data="channels" v-loading="channelsLoading" size="small" border style="width: 100%">
        <el-table-column prop="channel" label="货道" width="80" />
        <el-table-column prop="name" label="商品" min-width="160" show-overflow-tooltip />
        <el-table-column label="库存/容量" width="110">
          <template #default="scope">{{ scope.row.currCount }} / {{ scope.row.maxCount }}</template>
        </el-table-column>
        <el-table-column label="有货" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.hasStock === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.hasStock === 1 ? '有' : '无' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dayCount" label="临期(天)" width="90" />
        <el-table-column prop="manufactureEndDate" label="到期日" min-width="120">
          <template #default="scope">{{ formatDate(scope.row.manufactureEndDate) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface StockRow {
  vmId: number
  innerCode: string
  vmDesc: string
  nodeName?: string
  stockStatus: number | null
}

interface ChannelRow {
  channel: string
  name: string
  currCount: number
  maxCount: number
  hasStock: number
  dayCount: number
  manufactureEndDate: string | null
}

const rows = ref<StockRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterType = ref('1')
const filterText = ref('')

const channelsVisible = ref(false)
const channelsLoading = ref(false)
const currentVm = ref<StockRow | null>(null)
const channels = ref<ChannelRow[]>([])

function formatDate(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 10)
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
    const res = await api.get<{ total: number; data: StockRow[] }>(`/vms/api/vmStock/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载库存列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

async function openChannels(row: StockRow) {
  currentVm.value = row
  channelsVisible.value = true
  channelsLoading.value = true
  try {
    channels.value = await api.get<ChannelRow[]>(`/vms/api/vmStock/${row.vmId}/channels`)
  } catch (e) {
    ElMessage.error('加载货道库存失败')
  } finally {
    channelsLoading.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
.vm-stock h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
