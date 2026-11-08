<template>
  <div class="sku-event-strategy">
    <h1>商品促销策略</h1>
    <p class="hint">按商品设置折扣（%）或立减（分）促销，可绑定到售货机促销配置使用。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="策略名称">
        <el-input v-model="filterName" placeholder="策略名称" clearable style="width: 180px" @keyup.enter="loadList" />
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
      <el-table-column prop="eventCount" label="商品数" width="80" />
      <el-table-column label="创建" min-width="150">
        <template #default="scope">
          <div>{{ scope.row.ctorName || '-' }}</div>
          <div class="time">{{ formatTime(scope.row.ctime) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="更新" min-width="150">
        <template #default="scope">
          <div>{{ scope.row.utorName || '-' }}</div>
          <div class="time">{{ formatTime(scope.row.utime) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openView(scope.row)">查看</el-button>
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
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

    <!-- 查看 -->
    <el-dialog v-model="viewVisible" :title="`商品促销：${viewData?.strategyName || ''}`" width="680px">
      <el-table :data="viewData?.events || []" size="small" border style="width: 100%">
        <el-table-column label="商品" min-width="200">
          <template #default="scope">
            <div class="sku-cell">
              <img v-if="scope.row.skuImgUrl" :src="scope.row.skuImgUrl" class="sku-img" />
              <span>{{ scope.row.skuName }}（{{ scope.row.skuCode || '-' }}）</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="原价（分）" width="100">
          <template #default="scope">{{ scope.row.offerPrice ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="折扣类型" width="100">
          <template #default="scope">{{ discountTypeText(scope.row.discountType) }}</template>
        </el-table-column>
        <el-table-column label="折扣值" width="100">
          <template #default="scope">{{ scope.row.discountValue }}{{ scope.row.discountType === 1 ? '%' : ' 分' }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新建/编辑 -->
    <el-dialog v-model="editorVisible" :title="editorMode === 'create' ? '新建商品促销策略' : '编辑商品促销策略'" width="820px">
      <el-form :model="editor" label-width="90px">
        <el-form-item label="策略名称" required>
          <el-input v-model="editor.strategyName" maxlength="60" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editor.strategyDesc" type="textarea" :rows="2" maxlength="200" />
        </el-form-item>
      </el-form>

      <el-table :data="editor.items" size="small" border style="width: 100%">
        <el-table-column label="商品" min-width="220">
          <template #default="scope">
            <el-select
              v-model="scope.row.skuId"
              filterable
              remote
              :remote-method="searchSkus"
              :loading="skuLoading"
              placeholder="搜索商品"
              size="small"
              style="width: 100%"
              @change="onSkuChange(scope.row)"
            >
              <el-option v-for="s in skuOptions" :key="s.id" :label="`${s.skuName} (${s.skuCode || ''})`" :value="s.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="原价（分）" width="90">
          <template #default="scope">{{ scope.row.offerPrice ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="折扣类型" width="130">
          <template #default="scope">
            <el-select v-model="scope.row.discountType" size="small" style="width: 110px">
              <el-option label="折扣(%)" :value="1" />
              <el-option label="立减(分)" :value="2" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="折扣值" width="130">
          <template #default="scope">
            <el-input-number v-model="scope.row.discountValue" :min="0" :max="scope.row.discountType === 1 ? 100 : 999999" size="small" controls-position="right" style="width: 110px" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="70">
          <template #default="scope">
            <el-button link type="danger" size="small" @click="removeItem(scope.$index)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="actions">
        <el-button @click="addItem">添加商品</el-button>
      </div>

      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface StrategyRow {
  strategyId: number
  strategyName: string
  strategyDesc: string
  eventCount: number
  ctorName: string | null
  ctime: string | null
  utorName: string | null
  utime: string | null
}

interface EventRow {
  eventId: number
  skuId: number
  skuName: string
  skuCode: string
  skuImgUrl?: string
  offerPrice: number | null
  discountType: number
  discountValue: number
}

interface StrategyDetail {
  strategyId: number
  strategyName: string
  strategyDesc: string
  events: EventRow[]
}

interface EditorItem {
  skuId: number | undefined
  skuName: string
  offerPrice: number | null
  discountType: number
  discountValue: number | undefined
}

interface SkuOption {
  id: number
  skuName: string
  skuCode: string
  offerPrice: number | null
}

const rows = ref<StrategyRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterName = ref('')
const selectedRows = ref<StrategyRow[]>([])

const viewVisible = ref(false)
const viewData = ref<StrategyDetail | null>(null)

const editorVisible = ref(false)
const editorMode = ref<'create' | 'edit'>('create')
const saving = ref(false)
const editor = ref<{ strategyId?: number; strategyName: string; strategyDesc: string; items: EditorItem[] }>({
  strategyName: '',
  strategyDesc: '',
  items: [],
})
const skuOptions = ref<SkuOption[]>([])
const skuLoading = ref(false)

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

function discountTypeText(type: number) {
  return type === 1 ? '折扣(%)' : type === 2 ? '立减(分)' : '-'
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterName.value) params.set('searchValue', filterName.value)
    const res = await api.get<{ total: number; data: StrategyRow[] }>(`/vms/api/stSkuEvent/list?${params.toString()}`)
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

function onSkuChange(row: EditorItem) {
  const found = skuOptions.value.find((s) => s.id === row.skuId)
  if (found) {
    row.skuName = found.skuName
    row.offerPrice = found.offerPrice
  }
}

function buildEmptyItem(): EditorItem {
  return { skuId: undefined, skuName: '', offerPrice: null, discountType: 1, discountValue: undefined }
}

function addItem() {
  editor.value.items.push(buildEmptyItem())
}

function removeItem(index: number) {
  editor.value.items.splice(index, 1)
}

function openCreate() {
  editorMode.value = 'create'
  editor.value = { strategyName: '', strategyDesc: '', items: [buildEmptyItem()] }
  editorVisible.value = true
}

async function openView(row: StrategyRow) {
  viewVisible.value = true
  viewData.value = null
  try {
    viewData.value = await api.get<StrategyDetail>(`/vms/api/stSkuEvent/${row.strategyId}`)
  } catch (e) {
    ElMessage.error('加载策略详情失败')
  }
}

async function openEdit(row: StrategyRow) {
  editorMode.value = 'edit'
  editor.value = { strategyId: row.strategyId, strategyName: row.strategyName, strategyDesc: row.strategyDesc, items: [] }
  editorVisible.value = true
  try {
    const detail = await api.get<StrategyDetail>(`/vms/api/stSkuEvent/${row.strategyId}`)
    editor.value = {
      strategyId: detail.strategyId,
      strategyName: detail.strategyName,
      strategyDesc: detail.strategyDesc,
      items: (detail.events || []).map((e) => ({
        skuId: e.skuId,
        skuName: e.skuName,
        offerPrice: e.offerPrice,
        discountType: e.discountType,
        discountValue: e.discountValue,
      })),
    }
  } catch (e) {
    ElMessage.error('加载策略详情失败')
  }
}

async function save() {
  if (!editor.value.strategyName) {
    ElMessage.warning('请填写策略名称')
    return
  }
  const items = editor.value.items.filter((i) => i.skuId && i.discountValue !== undefined && i.discountValue !== null)
  if (!items.length) {
    ElMessage.warning('请至少添加一个商品促销')
    return
  }
  const invalid = items.find((i) => i.discountType === 1 && (i.discountValue as number) > 100)
  if (invalid) {
    ElMessage.warning('折扣百分比不能大于 100')
    return
  }
  saving.value = true
  try {
    const payload: Record<string, any> = {
      strategyName: editor.value.strategyName,
      strategyDesc: editor.value.strategyDesc,
      skuIds: items.map((i) => i.skuId).join(','),
      discountTypes: items.map((i) => i.discountType).join(','),
      discountValues: items.map((i) => i.discountValue).join(','),
    }
    if (editorMode.value === 'edit') payload.strategyId = editor.value.strategyId
    const res = await api.post<any>('/vms/api/stSkuEvent/save', payload, { form: true })
    handleResult(res, '保存成功')
    if (res?.success || res?.type === 'success') {
      editorVisible.value = false
      loadList()
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
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
    const res = await api.post<any>('/vms/api/stSkuEvent/delete', {
      ids: selectedRows.value.map((r) => r.strategyId).join(','),
    }, { form: true })
    handleResult(res, '删除成功')
    loadList()
  } catch (e) {
    ElMessage.error('删除失败')
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
.sku-event-strategy h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.time { color: #95a5a6; font-size: 12px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.actions { margin-top: 12px; }
.sku-cell { display: flex; align-items: center; gap: 8px; }
.sku-img { width: 32px; height: 32px; object-fit: contain; border: 1px solid #eee; }
</style>
