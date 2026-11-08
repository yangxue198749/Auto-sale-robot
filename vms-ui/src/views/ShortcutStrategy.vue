<template>
  <div class="shortcut-strategy">
    <h1>闪购键策略</h1>
    <p class="hint">按售货机型号配置每个闪购键绑定的商品或跳转页。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="策略名称">
        <el-input v-model="filterName" placeholder="策略名称" clearable style="width: 180px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item label="型号">
        <el-select v-model="filterModelId" filterable clearable placeholder="全部型号" style="width: 200px">
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
      <el-table-column prop="strategyName" label="策略名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="strategyDesc" label="描述" min-width="170" show-overflow-tooltip />
      <el-table-column prop="vmModelName" label="售货机型号" min-width="140" />
      <el-table-column prop="shortcutsQty" label="闪购键数" width="90" />
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
    <el-dialog v-model="viewVisible" title="闪购键策略详情" width="640px">
      <el-descriptions :column="2" border size="small" style="margin-bottom: 12px">
        <el-descriptions-item label="策略名称">{{ viewData?.strategyName }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ viewData?.vmModelName }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ viewData?.strategyDesc || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="viewData?.shortcuts || []" size="small" border>
        <el-table-column prop="shortcutsCode" label="键位" width="70" />
        <el-table-column label="类型" width="90">
          <template #default="scope">{{ bindTypeText(scope.row.bindType) }}</template>
        </el-table-column>
        <el-table-column label="内容" min-width="200">
          <template #default="scope">{{ scope.row.skuName || scope.row.resourceName || '-' }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新建/编辑 -->
    <el-dialog v-model="editorVisible" :title="editorMode === 'create' ? '新建闪购键策略' : '编辑闪购键策略'" width="720px">
      <el-form :model="editor" label-width="90px">
        <el-form-item label="策略名称" required>
          <el-input v-model="editor.strategyName" maxlength="60" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editor.strategyDesc" type="textarea" :rows="2" maxlength="200" />
        </el-form-item>
        <el-form-item label="售货机型号" required>
          <el-select v-model="editor.modelId" filterable placeholder="选择型号" :disabled="editorMode === 'edit'" style="width: 260px" @change="onModelChange">
            <el-option v-for="m in models" :key="m.id" :label="`${m.modelName}（${m.shortcutsQty} 键）`" :value="m.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="editor.rows" size="small" border style="width: 100%" v-if="editor.rows.length">
        <el-table-column label="闪购键" width="90">
          <template #default="scope">{{ scope.$index + 1 }}</template>
        </el-table-column>
        <el-table-column label="绑定类型" width="180">
          <template #default="scope">
            <el-radio-group v-model="scope.row.bindType" size="small" @change="onBindTypeChange(scope.row)">
              <el-radio :value="1">商品</el-radio>
              <el-radio :value="2">跳转页</el-radio>
              <el-radio :value="0">空</el-radio>
            </el-radio-group>
          </template>
        </el-table-column>
        <el-table-column label="绑定内容" min-width="280">
          <template #default="scope">
            <el-select
              v-if="scope.row.bindType === 1"
              v-model="scope.row.skuId"
              filterable
              remote
              :remote-method="searchSkus"
              :loading="skuLoading"
              placeholder="搜索商品"
              size="small"
              style="width: 100%"
            >
              <el-option v-for="s in skuOptions" :key="s.id" :label="`${s.skuName} (${s.skuCode || ''})`" :value="s.id" />
            </el-select>
            <el-select
              v-else-if="scope.row.bindType === 2"
              v-model="scope.row.resourceId"
              filterable
              placeholder="选择跳转页"
              size="small"
              style="width: 100%"
            >
              <el-option v-for="r in resources" :key="r.id" :label="r.resourceName" :value="r.id" />
            </el-select>
            <span v-else class="empty-text">不绑定</span>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!editor.rows.length" class="empty-text" style="padding: 20px 0">请先选择售货机型号</div>

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

interface ModelOption {
  id: number
  modelName: string
  shortcutsQty: number
}

interface StrategyRow {
  strategyId: number
  strategyName: string
  strategyDesc: string
  vmModelId: number | null
  vmModelName: string | null
  shortcutsQty: number | null
  ctorName: string | null
  ctime: string | null
  utorName: string | null
  utime: string | null
}

interface ShortcutRow {
  shortcutsCode: string
  bindType: number
  skuId: number | null
  skuName?: string
  resourceId?: number
  resourceName?: string
}

interface StrategyDetail {
  strategyId: number
  strategyName: string
  strategyDesc: string
  vmModelId: number
  vmModelName: string
  shortcutsQty: number
  shortcuts: ShortcutRow[]
}

interface EditorRow {
  bindType: number
  skuId: number | undefined
  resourceId: number | undefined
}

interface ResourceRow {
  id: number
  resourceName: string
}

interface SkuOption {
  id: number
  skuName: string
  skuCode: string
}

const models = ref<ModelOption[]>([])
const resources = ref<ResourceRow[]>([])
const rows = ref<StrategyRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterName = ref('')
const filterModelId = ref<number | undefined>(undefined)
const selectedRows = ref<StrategyRow[]>([])

const viewVisible = ref(false)
const viewData = ref<StrategyDetail | null>(null)

const editorVisible = ref(false)
const editorMode = ref<'create' | 'edit'>('create')
const saving = ref(false)
const editor = ref<{ strategyId?: number; strategyName: string; strategyDesc: string; modelId: number | undefined; rows: EditorRow[] }>({
  strategyName: '',
  strategyDesc: '',
  modelId: undefined,
  rows: [],
})

const skuOptions = ref<SkuOption[]>([])
const skuLoading = ref(false)

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

function bindTypeText(type: number) {
  if (type === 1) return '商品'
  if (type === 2) return '跳转页'
  return '空'
}

async function loadModels() {
  try {
    const res = await api.get<{ data: ModelOption[] }>('/vms/api/vmModel/list?page=1&pageSize=100')
    models.value = res.data.map((m: any) => ({ id: m.id, modelName: m.modelName, shortcutsQty: m.shortcutsQty || 0 }))
  } catch (e) {
    ElMessage.error('加载型号失败')
  }
}

async function loadResources() {
  try {
    resources.value = await api.get<ResourceRow[]>('/vms/api/stShortcuts/resources')
  } catch (e) {
    // 资源列表失败不阻塞页面
  }
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterName.value) params.set('strategyName', filterName.value)
    if (filterModelId.value) params.set('modelId', String(filterModelId.value))
    const res = await api.get<{ total: number; data: StrategyRow[] }>(`/vms/api/stShortcuts/list?${params.toString()}`)
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

function onModelChange() {
  const model = models.value.find((m) => m.id === editor.value.modelId)
  const qty = model?.shortcutsQty || 0
  const existing = editor.value.rows
  const next: EditorRow[] = []
  for (let i = 0; i < qty; i++) {
    next.push(existing[i] || { bindType: 0, skuId: undefined, resourceId: undefined })
  }
  editor.value.rows = next
}

function onBindTypeChange(row: EditorRow) {
  if (row.bindType !== 1) row.skuId = undefined
  if (row.bindType !== 2) row.resourceId = undefined
}

function openCreate() {
  editorMode.value = 'create'
  editor.value = { strategyName: '', strategyDesc: '', modelId: undefined, rows: [] }
  editorVisible.value = true
}

async function openEdit(row: StrategyRow) {
  editorMode.value = 'edit'
  editor.value = { strategyId: row.strategyId, strategyName: row.strategyName, strategyDesc: row.strategyDesc, modelId: row.vmModelId || undefined, rows: [] }
  editorVisible.value = true
  try {
    const detail = await api.get<StrategyDetail>(`/vms/api/stShortcuts/${row.strategyId}`)
    const qty = detail.shortcutsQty || 0
    const next: EditorRow[] = []
    for (let i = 0; i < qty; i++) {
      const code = String(i + 1)
      const existing = (detail.shortcuts || []).find((s) => String(s.shortcutsCode) === code)
      next.push({
        bindType: existing?.bindType ?? 0,
        skuId: existing?.skuId ?? undefined,
        resourceId: existing?.resourceId ?? undefined,
      })
    }
    editor.value.rows = next
  } catch (e) {
    ElMessage.error('加载策略详情失败')
  }
}

async function openView(row: StrategyRow) {
  viewVisible.value = true
  viewData.value = null
  try {
    viewData.value = await api.get<StrategyDetail>(`/vms/api/stShortcuts/${row.strategyId}`)
  } catch (e) {
    ElMessage.error('加载策略详情失败')
  }
}

async function save() {
  if (!editor.value.strategyName) {
    ElMessage.warning('请填写策略名称')
    return
  }
  if (!editor.value.modelId) {
    ElMessage.warning('请选择售货机型号')
    return
  }
  saving.value = true
  try {
    const bindTypes: string[] = []
    const skuIds: string[] = []
    const resourceIds: string[] = []
    for (const r of editor.value.rows) {
      bindTypes.push(String(r.bindType))
      skuIds.push(r.bindType === 1 && r.skuId ? String(r.skuId) : '')
      resourceIds.push(r.bindType === 2 && r.resourceId ? String(r.resourceId) : '')
    }
    const payload: Record<string, any> = {
      strategyName: editor.value.strategyName,
      strategyDesc: editor.value.strategyDesc,
      modelId: editor.value.modelId,
      bindTypes: bindTypes.join(','),
      skuIds: skuIds.join(','),
      resourceIds: resourceIds.join(','),
    }
    let path = '/vms/api/stShortcuts/create'
    if (editorMode.value === 'edit') {
      payload.strategyId = editor.value.strategyId
      path = '/vms/api/stShortcuts/edit'
    }
    const res = await api.post<any>(path, payload, { form: true })
    handleResult(res, '保存成功')
    editorVisible.value = false
    loadList()
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
    const res = await api.post<any>('/vms/api/stShortcuts/delete', {
      ids: selectedRows.value.map((r) => r.strategyId).join(','),
    }, { form: true })
    handleResult(res, '删除成功')
    loadList()
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
  loadResources()
  loadList()
})
</script>

<style scoped>
.shortcut-strategy h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.time { color: #95a5a6; font-size: 12px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.empty-text { color: #95a5a6; font-size: 12px; }
</style>
