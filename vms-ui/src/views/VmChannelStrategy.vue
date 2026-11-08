<template>
  <div class="channel-strategy">
    <h1>货道策略</h1>
    <p class="hint">按售货机型号维护货道模板：关联商品、合并/拆分货道、设置满量，并调整商品列表页顺序。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="策略名称">
        <el-input v-model="searchName" placeholder="策略名称" clearable style="width: 180px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item label="型号">
        <el-select v-model="searchModelId" filterable clearable placeholder="全部型号" style="width: 200px">
          <el-option v-for="m in models" :key="m.id" :label="m.modelName" :value="m.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openCreate">新建策略</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="deleteSelected">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="strategyName" label="策略名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="strategyDesc" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column prop="vmModelName" label="售货机型号" min-width="140" />
      <el-table-column label="创建" min-width="160">
        <template #default="scope">
          <div>{{ scope.row.ctorName || '-' }}</div>
          <div class="time">{{ formatTime(scope.row.ctime) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="更新" min-width="160">
        <template #default="scope">
          <div>{{ scope.row.utorName || '-' }}</div>
          <div class="time">{{ formatTime(scope.row.utime) }}</div>
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

    <!-- 新建策略 -->
    <el-dialog v-model="createVisible" title="新建货道策略" width="480px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="售货机型号" required>
          <el-select v-model="createForm.vmModelId" filterable placeholder="选择型号" style="width: 100%">
            <el-option v-for="m in models" :key="m.id" :label="m.modelName" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="策略名称" required>
          <el-input v-model="createForm.strategyName" maxlength="60" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.strategyDesc" type="textarea" :rows="2" maxlength="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="createStrategy">创建</el-button>
      </template>
    </el-dialog>

    <!-- 策略详情 -->
    <el-dialog v-model="detailVisible" :title="`货道策略：${detail?.strategyName || ''}`" width="90%" top="4vh">
      <div v-loading="detailLoading">
        <el-descriptions :column="3" border size="small" style="margin-bottom: 12px">
          <el-descriptions-item label="型号">{{ detail?.vmModelName }}</el-descriptions-item>
          <el-descriptions-item label="层数/每层货道">{{ detail?.channelLayerQty }} 层（{{ detail?.channelPerLayer }}）</el-descriptions-item>
          <el-descriptions-item label="描述">{{ detail?.strategyDesc || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 操作栏 -->
        <div class="toolbar">
          <span class="selected-info">已选 {{ selectedChannelIds.length }} 个货道</span>
          <el-select
            v-model="relateSkuId"
            filterable
            remote
            :remote-method="searchSkus"
            :loading="skuLoading"
            placeholder="搜索商品"
            style="width: 240px"
          >
            <el-option v-for="s in skuOptions" :key="s.id" :label="`${s.skuName} (${s.skuCode || ''})`" :value="s.id" />
          </el-select>
          <el-button type="primary" :disabled="!selectedChannelIds.length || !relateSkuId" @click="relateSku">关联商品</el-button>
          <el-button :disabled="!selectedChannelIds.length" @click="cancelRelate">取消关联</el-button>
          <el-button :disabled="selectedChannelIds.length !== 2" @click="mergeChannels">合并货道</el-button>
          <el-button :disabled="selectedChannelIds.length !== 1" @click="splitChannel">拆分货道</el-button>
          <el-input-number v-model="fullValue" :min="0" size="small" style="width: 110px" controls-position="right" />
          <el-button :disabled="selectedChannelIds.length !== 1" @click="setFull">设置满量</el-button>
        </div>

        <!-- 货道网格 -->
        <div class="grid">
          <div v-for="row in gridRows" :key="row.row" class="grid-row">
            <div class="row-label">{{ row.row }} 层</div>
            <div class="row-channels">
              <div
                v-for="c in row.channels"
                :key="c.channelId"
                class="channel-card"
                :class="{ selected: selectedChannelIds.includes(c.channelId), merged: (c.mergeQty || 1) > 1 }"
                :style="{ width: `calc(${100 / (row.cols || 1) * (c.mergeQty || 1)}% - 6px)` }"
                @click="toggleChannel(c.channelId)"
              >
                <div class="code">
                  {{ c.channelCode }}
                  <span v-if="(c.mergeQty || 1) > 1" class="merge-tag">×{{ c.mergeQty }}</span>
                </div>
                <img v-if="c.skuImgUrl" :src="c.skuImgUrl" class="sku-img" />
                <div class="sku-name" :title="c.skuName">{{ c.skuName || '空货道' }}</div>
                <div class="capacity">满 {{ c.adjustMaxCapacity ?? c.maxCapacity ?? '-' }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 列表页排序 -->
        <el-card shadow="never" style="margin-top: 16px">
          <template #header>商品列表页顺序（前 15 个为第 1 页）</template>
          <el-table :data="detail?.sorts || []" size="small" border style="width: 100%">
            <el-table-column prop="sort" label="序号" width="70" />
            <el-table-column label="商品" min-width="200">
              <template #default="scope">
                <div class="sort-sku">
                  <img v-if="scope.row.skuImgUrl" :src="scope.row.skuImgUrl" class="sort-img" />
                  <span>{{ scope.row.skuName || '-' }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220">
              <template #default="scope">
                <el-button link size="small" :disabled="scope.$index === 0" @click="moveSort(scope.$index, -1)">上移</el-button>
                <el-button link size="small" :disabled="scope.$index === (detail?.sorts?.length || 0) - 1" @click="moveSort(scope.$index, 1)">下移</el-button>
                <el-button link size="small" @click="copySort(scope.row)">复制</el-button>
                <el-button link type="danger" size="small" @click="deleteSort(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface ModelOption {
  id: number
  modelName: string
}

interface StrategyRow {
  strategyId: number
  strategyName: string
  strategyDesc: string
  vmModelId: number | null
  vmModelName: string | null
  ctorName: string | null
  ctime: string | null
  utorName: string | null
  utime: string | null
}

interface ChannelRow {
  channelId: number
  channelCode: string
  skuId: number | null
  skuName?: string
  skuUnit?: string
  skuImgUrl?: string
  mergeQty: number
  maxCapacity: number | null
  adjustMaxCapacity: number | null
}

interface SortRow {
  sortId: number
  sort: number
  skuId: number
  skuName?: string
  skuImgUrl?: string
}

interface StrategyDetail {
  strategyId: number
  strategyName: string
  strategyDesc: string
  vmModelId: number
  vmModelName: string
  channelLayerQty: number
  channelPerLayer: string
  allowMerge: number
  channels: ChannelRow[]
  sorts: SortRow[]
}

interface SkuOption {
  id: number
  skuName: string
  skuCode: string
}

const models = ref<ModelOption[]>([])
const rows = ref<StrategyRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchName = ref('')
const searchModelId = ref<number | undefined>(undefined)
const selectedRows = ref<StrategyRow[]>([])

const createVisible = ref(false)
const creating = ref(false)
const createForm = ref({ vmModelId: undefined as number | undefined, strategyName: '', strategyDesc: '' })

const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref<StrategyDetail | null>(null)
const selectedChannelIds = ref<number[]>([])
const relateSkuId = ref<number | undefined>(undefined)
const fullValue = ref(0)
const skuOptions = ref<SkuOption[]>([])
const skuLoading = ref(false)

const gridRows = computed(() => {
  if (!detail.value) return []
  const per = (detail.value.channelPerLayer || '').split(',').map((n) => Number(n) || 0)
  const result: { row: number; cols: number; channels: ChannelRow[] }[] = []
  for (let r = 1; r <= per.length; r++) {
    const cols = per[r - 1]
    const channels = (detail.value.channels || [])
      .filter((c) => (c.channelCode || '').split('-')[0] === String(r) && c.mergeQty !== 0)
      .sort((a, b) => Number(a.channelCode.split('-')[1]) - Number(b.channelCode.split('-')[1]))
    result.push({ row: r, cols, channels })
  }
  return result
})

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

async function loadModels() {
  try {
    const res = await api.get<{ data: ModelOption[] }>('/vms/api/vmModel/list?page=1&pageSize=100')
    models.value = res.data
  } catch (e) {
    ElMessage.error('加载型号失败')
  }
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (searchName.value) params.set('searchStVmName', searchName.value)
    if (searchModelId.value) params.set('searchVmModelId', String(searchModelId.value))
    const res = await api.get<{ total: number; data: StrategyRow[] }>(`/vms/api/stVmChannel/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载策略列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: StrategyRow[]) {
  selectedRows.value = selection
}

function openCreate() {
  createForm.value = { vmModelId: undefined, strategyName: '', strategyDesc: '' }
  createVisible.value = true
}

async function createStrategy() {
  if (!createForm.value.vmModelId || !createForm.value.strategyName) {
    ElMessage.warning('请选择型号并填写策略名称')
    return
  }
  creating.value = true
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/create', {
      vmModelId: createForm.value.vmModelId,
      strategyName: createForm.value.strategyName,
      strategyDesc: createForm.value.strategyDesc,
    }, { form: true })
    handleResult(res, '创建成功')
    createVisible.value = false
    loadList()
  } catch (e) {
    ElMessage.error('创建失败')
  } finally {
    creating.value = false
  }
}

async function deleteSelected() {
  if (!selectedRows.value.length) return
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个策略？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/delete', {
      ids: selectedRows.value.map((r) => r.strategyId).join(','),
    }, { form: true })
    handleResult(res, '删除成功')
    loadList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

async function openDetail(row: StrategyRow) {
  detailVisible.value = true
  selectedChannelIds.value = []
  relateSkuId.value = undefined
  await loadDetail(row.strategyId)
}

async function loadDetail(strategyId: number) {
  detailLoading.value = true
  try {
    detail.value = await api.get<StrategyDetail>(`/vms/api/stVmChannel/${strategyId}`)
    // 清理已不存在的选中项
    const ids = new Set((detail.value.channels || []).map((c) => c.channelId))
    selectedChannelIds.value = selectedChannelIds.value.filter((id) => ids.has(id))
  } catch (e) {
    ElMessage.error('加载策略详情失败')
  } finally {
    detailLoading.value = false
  }
}

function toggleChannel(channelId: number) {
  const idx = selectedChannelIds.value.indexOf(channelId)
  if (idx >= 0) selectedChannelIds.value.splice(idx, 1)
  else selectedChannelIds.value.push(channelId)
}

async function searchSkus(query: string) {
  if (!query) {
    skuOptions.value = []
    return
  }
  skuLoading.value = true
  try {
    const res = await api.get<{ data: SkuOption[] }>(`/vms/api/sku/list?page=1&pageSize=20&name=${encodeURIComponent(query)}`)
    skuOptions.value = res.data
  } catch (e) {
    ElMessage.error('搜索商品失败')
  } finally {
    skuLoading.value = false
  }
}

async function relateSku() {
  if (!detail.value || !relateSkuId.value) return
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/relateSku', {
      channelIds: selectedChannelIds.value.join(','),
      skuId: relateSkuId.value,
    }, { form: true })
    handleResult(res, '关联商品成功')
    if (res?.type === 'warn') ElMessage.warning(res.message)
    await loadDetail(detail.value.strategyId)
  } catch (e) {
    ElMessage.error('关联商品失败')
  }
}

async function cancelRelate() {
  if (!detail.value) return
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/cancelRelate', {
      channelIds: selectedChannelIds.value.join(','),
    }, { form: true })
    handleResult(res, '取消关联成功')
    await loadDetail(detail.value.strategyId)
  } catch (e) {
    ElMessage.error('取消关联失败')
  }
}

async function mergeChannels() {
  if (!detail.value || selectedChannelIds.value.length !== 2) return
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/mergeChannel', {
      firstChannelId: selectedChannelIds.value[0],
      secondChannelId: selectedChannelIds.value[1],
    }, { form: true })
    handleResult(res, '合并成功')
    selectedChannelIds.value = []
    await loadDetail(detail.value.strategyId)
  } catch (e) {
    ElMessage.error('合并失败')
  }
}

async function splitChannel() {
  if (!detail.value || selectedChannelIds.value.length !== 1) return
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/cancelMerge', {
      channelId: selectedChannelIds.value[0],
    }, { form: true })
    handleResult(res, '拆分成功')
    await loadDetail(detail.value.strategyId)
  } catch (e) {
    ElMessage.error('拆分失败')
  }
}

async function setFull() {
  if (!detail.value || selectedChannelIds.value.length !== 1) return
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/setFull', {
      channelId: selectedChannelIds.value[0],
      fullval: fullValue.value,
    }, { form: true })
    handleResult(res, '设置成功')
    await loadDetail(detail.value.strategyId)
  } catch (e) {
    ElMessage.error('设置满量失败')
  }
}

async function moveSort(index: number, offset: number) {
  if (!detail.value) return
  const sorts = detail.value.sorts || []
  const target = sorts[index + offset]
  const current = sorts[index]
  if (!target || !current) return
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/changeSort', {
      firstSortId: current.sortId,
      secondSortId: target.sortId,
    }, { form: true })
    handleResult(res, '调整成功')
    await loadDetail(detail.value.strategyId)
  } catch (e) {
    ElMessage.error('调整失败')
  }
}

async function copySort(row: SortRow) {
  if (!detail.value) return
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/copySort', { sortId: row.sortId }, { form: true })
    handleResult(res, '复制成功')
    await loadDetail(detail.value.strategyId)
  } catch (e) {
    ElMessage.error('复制失败')
  }
}

async function deleteSort(row: SortRow) {
  if (!detail.value) return
  try {
    const res = await api.post<any>('/vms/api/stVmChannel/deleteSort', { sortId: row.sortId }, { form: true })
    handleResult(res, '删除成功')
    await loadDetail(detail.value.strategyId)
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function handleResult(res: any, successMessage: string) {
  if (res?.success || res?.type === 'success') {
    ElMessage.success(res?.message || successMessage)
  } else {
    ElMessage.error(res?.message || '操作失败')
  }
}

onMounted(() => {
  loadModels()
  loadList()
})
</script>

<style scoped>
.channel-strategy h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.time { color: #95a5a6; font-size: 12px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.toolbar { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; margin-bottom: 12px; }
.selected-info { color: #2c3e50; font-size: 13px; }
.grid { border: 1px solid #ebeef5; border-radius: 4px; padding: 10px; }
.grid-row { display: flex; align-items: stretch; gap: 6px; margin-bottom: 6px; }
.row-label { width: 48px; display: flex; align-items: center; justify-content: center; color: #7f8c8d; font-size: 12px; }
.row-channels { flex: 1; display: flex; gap: 6px; }
.channel-card {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 6px;
  cursor: pointer;
  text-align: center;
  background: #fff;
  transition: all 0.15s;
  min-width: 0;
}
.channel-card:hover { border-color: #409eff; }
.channel-card.selected { border-color: #409eff; background: #ecf5ff; box-shadow: 0 0 0 1px #409eff inset; }
.channel-card.merged { background: #fdf6ec; }
.code { font-weight: 600; font-size: 12px; color: #303133; }
.merge-tag { color: #e6a23c; font-size: 11px; }
.sku-img { width: 100%; height: 48px; object-fit: contain; }
.sku-name { font-size: 12px; color: #606266; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.capacity { font-size: 11px; color: #909399; }
.sort-sku { display: flex; align-items: center; gap: 8px; }
.sort-img { width: 32px; height: 32px; object-fit: contain; border: 1px solid #eee; }
</style>
