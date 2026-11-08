<template>
  <div class="vm-event-strategy">
    <h1>售货机促销策略</h1>
    <p class="hint">整机促销：对售货机内全部商品应用统一折扣或立减。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="策略名称">
        <el-input v-model="filterName" placeholder="策略名称" clearable style="width: 180px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item label="折扣类型">
        <el-select v-model="filterType" clearable placeholder="全部" style="width: 140px">
          <el-option label="百分比" value="1" />
          <el-option label="绝对值" value="2" />
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
      <el-table-column label="折扣类型" width="110">
        <template #default="scope">{{ discountTypeText(scope.row.discountType) }}</template>
      </el-table-column>
      <el-table-column label="折扣值" width="110">
        <template #default="scope">{{ scope.row.discountValue }}{{ scope.row.discountType === 1 ? '%' : ' 分' }}</template>
      </el-table-column>
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
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="scope">
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

    <el-dialog v-model="editorVisible" :title="editorMode === 'create' ? '新建售货机促销策略' : '编辑售货机促销策略'" width="520px">
      <el-form :model="editor" label-width="90px">
        <el-form-item label="策略名称" required>
          <el-input v-model="editor.strategyName" maxlength="60" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editor.strategyDesc" type="textarea" :rows="2" maxlength="200" />
        </el-form-item>
        <el-form-item label="折扣类型" required>
          <el-radio-group v-model="editor.discountType">
            <el-radio :value="1">百分比(%)</el-radio>
            <el-radio :value="2">绝对值(分)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="折扣值" required>
          <el-input-number
            v-model="editor.discountValue"
            :min="0"
            :max="editor.discountType === 1 ? 100 : 999999"
            controls-position="right"
            style="width: 160px"
          />
          <span class="unit">{{ editor.discountType === 1 ? '%' : '分' }}</span>
        </el-form-item>
      </el-form>
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
  discountType: number | null
  discountValue: number | null
  ctorName: string | null
  ctime: string | null
  utorName: string | null
  utime: string | null
}

const rows = ref<StrategyRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterName = ref('')
const filterType = ref<string | undefined>(undefined)
const selectedRows = ref<StrategyRow[]>([])

const editorVisible = ref(false)
const editorMode = ref<'create' | 'edit'>('create')
const saving = ref(false)
const editor = ref<{ strategyId?: number; strategyName: string; strategyDesc: string; discountType: number; discountValue: number | undefined }>({
  strategyName: '',
  strategyDesc: '',
  discountType: 1,
  discountValue: undefined,
})

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

function discountTypeText(type: number | null) {
  if (type === 1) return '百分比'
  if (type === 2) return '绝对值'
  return '-'
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterName.value) params.set('searchValue', filterName.value)
    if (filterType.value) params.set('selectValue', filterType.value)
    const res = await api.get<{ total: number; data: StrategyRow[] }>(`/vms/api/stVmEvent/list?${params.toString()}`)
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
  editorMode.value = 'create'
  editor.value = { strategyName: '', strategyDesc: '', discountType: 1, discountValue: undefined }
  editorVisible.value = true
}

async function openEdit(row: StrategyRow) {
  editorMode.value = 'edit'
  editor.value = {
    strategyId: row.strategyId,
    strategyName: row.strategyName,
    strategyDesc: row.strategyDesc,
    discountType: row.discountType ?? 1,
    discountValue: row.discountValue ?? undefined,
  }
  editorVisible.value = true
}

async function save() {
  if (!editor.value.strategyName) {
    ElMessage.warning('请填写策略名称')
    return
  }
  if (editor.value.discountValue === undefined || editor.value.discountValue === null) {
    ElMessage.warning('请填写折扣值')
    return
  }
  if (editor.value.discountType === 1 && (editor.value.discountValue as number) > 100) {
    ElMessage.warning('折扣百分比不能大于 100')
    return
  }
  saving.value = true
  try {
    const payload: Record<string, any> = {
      strategyName: editor.value.strategyName,
      strategyDesc: editor.value.strategyDesc,
      discountType: editor.value.discountType,
      discountValue: editor.value.discountValue,
    }
    if (editorMode.value === 'edit') payload.strategyId = editor.value.strategyId
    const res = await api.post<any>('/vms/api/stVmEvent/save', payload, { form: true })
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
    const res = await api.post<any>('/vms/api/stVmEvent/delete', {
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
.vm-event-strategy h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.time { color: #95a5a6; font-size: 12px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.unit { margin-left: 6px; color: #909399; }
</style>
