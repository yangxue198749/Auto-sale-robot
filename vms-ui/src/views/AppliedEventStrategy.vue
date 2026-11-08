<template>
  <div class="applied-event">
    <h1>应用促销策略</h1>
    <p class="hint">将商品促销/整机促销策略应用到售货机，设置生效日期与时段。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="设备编号">
        <el-input v-model="filterInnerCode" placeholder="设备编号" clearable style="width: 160px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item label="点位">
        <el-input v-model="filterNodeName" placeholder="点位名称" clearable style="width: 160px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openApply('sku')">应用商品促销</el-button>
        <el-button type="success" @click="openApply('vm')">应用整机促销</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="cancelSelected">取消应用</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="eventName" label="促销名称" min-width="150" show-overflow-tooltip />
      <el-table-column label="类型" width="90">
        <template #default="scope">{{ scope.row.eventType === 1 ? '整机' : '商品' }}</template>
      </el-table-column>
      <el-table-column prop="innerCode" label="设备编号" width="120" />
      <el-table-column prop="nodeName" label="点位" min-width="130" show-overflow-tooltip />
      <el-table-column label="生效日期" width="190">
        <template #default="scope">{{ scope.row.beginDate }} ~ {{ scope.row.endDate }}</template>
      </el-table-column>
      <el-table-column label="时段" width="110">
        <template #default="scope">{{ scope.row.startTime }} ~ {{ scope.row.endTime }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '进行中' ? 'success' : scope.row.status === '未开始' ? 'warning' : 'info'" size="small">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row)">查看</el-button>
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

    <!-- 详情 -->
    <el-dialog v-model="detailVisible" :title="`促销详情：${detail?.eventName || ''}`" width="720px">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 12px">
        <el-descriptions-item label="设备">{{ detail?.innerCode }}（{{ detail?.vmDesc || '-' }}）</el-descriptions-item>
        <el-descriptions-item label="类型">{{ detail?.eventType === 1 ? '整机促销' : '商品促销' }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ detail?.beginDate }} ~ {{ detail?.endDate }}</el-descriptions-item>
        <el-descriptions-item label="时段">{{ detail?.startTime }} ~ {{ detail?.endTime }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail?.events || []" size="small" border style="width: 100%">
        <el-table-column label="商品" min-width="200">
          <template #default="scope">
            <div class="sku-cell">
              <img v-if="scope.row.skuImgUrl" :src="scope.row.skuImgUrl" class="sku-img" />
              <span>{{ scope.row.skuName || '整机全部商品' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="折扣类型" width="100">
          <template #default="scope">{{ scope.row.discountType === 1 ? '折扣(%)' : '立减(分)' }}</template>
        </el-table-column>
        <el-table-column label="折扣值" width="100">
          <template #default="scope">{{ scope.row.discountValue }}{{ scope.row.discountType === 1 ? '%' : ' 分' }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 应用促销 -->
    <el-dialog v-model="applyVisible" :title="applyType === 'sku' ? '应用商品促销' : '应用整机促销'" width="820px">
      <el-form :model="applyForm" label-width="90px">
        <el-form-item label="促销策略" required>
          <el-select v-model="applyForm.strategyId" filterable placeholder="选择策略" style="width: 320px">
            <el-option
              v-for="s in strategies"
              :key="s.strategyId"
              :label="`${s.strategyName}${s.discountType ? '（' + (s.discountType === 1 ? s.discountValue + '%' : s.discountValue + '分') + '）' : ''}`"
              :value="s.strategyId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="生效日期" required>
          <el-date-picker
            v-model="applyForm.dateRange"
            type="daterange"
            value-format="yyyy-MM-dd"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 320px"
          />
        </el-form-item>
        <el-form-item label="生效时段" required>
          <el-time-select v-model="applyForm.startTime" start="00:00" step="00:30" end="23:30" placeholder="开始时间" style="width: 130px" />
          <span class="range-sep">至</span>
          <el-time-select v-model="applyForm.endTime" start="00:00" step="00:30" end="23:30" placeholder="结束时间" style="width: 130px" />
        </el-form-item>
      </el-form>

      <div class="candidate-head">
        <span>选择售货机（仅显示未设置同类促销的设备）</span>
        <el-input v-model="candidateFilter" placeholder="按编号/点位筛选" clearable size="small" style="width: 200px" @keyup.enter="loadCandidates" />
        <el-button size="small" @click="loadCandidates">筛选</el-button>
      </div>
      <el-table
        :data="candidates"
        v-loading="candidateLoading"
        size="small"
        border
        max-height="300"
        style="width: 100%"
        @selection-change="onCandidateChange"
      >
        <el-table-column type="selection" width="45" />
        <el-table-column prop="innerCode" label="设备编号" width="130" />
        <el-table-column prop="vmDesc" label="设备名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="nodeName" label="点位" min-width="140" show-overflow-tooltip />
      </el-table>

      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" :loading="applying" @click="apply">应用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface AppliedRow {
  eventId: number
  eventName: string
  eventDesc: string
  eventType: number
  vmId: number | null
  innerCode: string | null
  vmDesc: string | null
  nodeName: string | null
  beginDate: string | null
  endDate: string | null
  startTime: string | null
  endTime: string | null
  status: string
}

interface EventRow {
  skuId?: number
  skuName?: string
  skuCode?: string
  skuImgUrl?: string
  offerPrice?: number | null
  discountType: number
  discountValue: number
}

interface AppliedDetail extends AppliedRow {
  events: EventRow[]
}

interface StrategyOption {
  strategyId: number
  strategyName: string
  strategyDesc: string
  discountType?: number
  discountValue?: number
}

interface CandidateRow {
  vmId: number
  innerCode: string
  vmDesc: string
  nodeName?: string
}

const rows = ref<AppliedRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterInnerCode = ref('')
const filterNodeName = ref('')
const selectedRows = ref<AppliedRow[]>([])

const detailVisible = ref(false)
const detail = ref<AppliedDetail | null>(null)

const applyVisible = ref(false)
const applyType = ref<'sku' | 'vm'>('sku')
const applying = ref(false)
const strategies = ref<StrategyOption[]>([])
const candidates = ref<CandidateRow[]>([])
const candidateLoading = ref(false)
const candidateFilter = ref('')
const selectedCandidates = ref<CandidateRow[]>([])
const applyForm = ref<{
  strategyId: number | undefined
  dateRange: string[]
  startTime: string
  endTime: string
}>({ strategyId: undefined, dateRange: [], startTime: '', endTime: '' })

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterInnerCode.value) params.set('searchInnerCode', filterInnerCode.value)
    if (filterNodeName.value) params.set('searchNodeName', filterNodeName.value)
    const res = await api.get<{ total: number; data: AppliedRow[] }>(`/vms/api/mtVmEvent/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载促销列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: AppliedRow[]) {
  selectedRows.value = selection
}

async function openDetail(row: AppliedRow) {
  detailVisible.value = true
  detail.value = null
  try {
    detail.value = await api.get<AppliedDetail>(`/vms/api/mtVmEvent/${row.eventId}`)
  } catch (e) {
    ElMessage.error('加载促销详情失败')
  }
}

async function cancelSelected() {
  if (!selectedRows.value.length) return
  try {
    await ElMessageBox.confirm(`确定取消选中的 ${selectedRows.value.length} 条促销应用？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/mtVmEvent/delete', {
      ids: selectedRows.value.map((r) => r.eventId).join(','),
    }, { form: true })
    handleResult(res, '取消成功')
    loadList()
  } catch (e) {
    ElMessage.error('取消失败')
  }
}

async function openApply(type: 'sku' | 'vm') {
  applyType.value = type
  applyForm.value = { strategyId: undefined, dateRange: [], startTime: '', endTime: '' }
  candidateFilter.value = ''
  candidates.value = []
  selectedCandidates.value = []
  applyVisible.value = true
  await Promise.all([loadStrategies(), loadCandidates()])
}

async function loadStrategies() {
  try {
    strategies.value = await api.get<StrategyOption[]>(`/vms/api/mtVmEvent/strategies/${applyType.value}`)
  } catch (e) {
    ElMessage.error('加载促销策略失败')
  }
}

async function loadCandidates() {
  candidateLoading.value = true
  try {
    const params = new URLSearchParams()
    const keyword = candidateFilter.value.trim()
    if (keyword) {
      params.set('searchInnerCode', keyword)
    }
    const query = params.toString() ? `?${params.toString()}` : ''
    candidates.value = await api.get<CandidateRow[]>(`/vms/api/mtVmEvent/candidates/${applyType.value}${query}`)
  } catch (e) {
    ElMessage.error('加载可选设备失败')
  } finally {
    candidateLoading.value = false
  }
}

function onCandidateChange(selection: CandidateRow[]) {
  selectedCandidates.value = selection
}

async function apply() {
  if (!applyForm.value.strategyId) {
    ElMessage.warning('请选择促销策略')
    return
  }
  if (!applyForm.value.dateRange || applyForm.value.dateRange.length !== 2) {
    ElMessage.warning('请选择生效日期')
    return
  }
  if (!applyForm.value.startTime || !applyForm.value.endTime) {
    ElMessage.warning('请选择生效时段')
    return
  }
  if (!selectedCandidates.value.length) {
    ElMessage.warning('请选择要应用促销的售货机')
    return
  }
  applying.value = true
  try {
    const res = await api.post<any>('/vms/api/mtVmEvent/apply', {
      eventType: applyType.value === 'vm' ? 1 : 2,
      strategyId: applyForm.value.strategyId,
      vmIds: selectedCandidates.value.map((c) => c.vmId).join(','),
      beginDate: applyForm.value.dateRange[0],
      endDate: applyForm.value.dateRange[1],
      startTime: applyForm.value.startTime,
      endTime: applyForm.value.endTime,
    }, { form: true })
    handleResult(res, '应用成功')
    if (res?.success || res?.type === 'success') {
      applyVisible.value = false
      loadList()
    }
  } catch (e) {
    ElMessage.error('应用失败')
  } finally {
    applying.value = false
  }
}

function handleResult(res: any, successMessage: string) {
  if (res?.success || res?.type === 'success' || res?.code === 0) {
    ElMessage.success(res?.message || successMessage)
  } else {
    ElMessage.error(res?.message || '操作失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.applied-event h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.candidate-head { display: flex; align-items: center; gap: 8px; margin: 8px 0; color: #606266; font-size: 13px; }
.range-sep { margin: 0 8px; color: #909399; }
.sku-cell { display: flex; align-items: center; gap: 8px; }
.sku-img { width: 32px; height: 32px; object-fit: contain; border: 1px solid #eee; }
</style>
