<template>
  <div class="text-strategy">
    <h1>走马灯策略</h1>
    <p class="hint">维护设备屏幕走马灯文案策略，可绑定到售货机使用。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="策略名称">
        <el-input v-model="filterName" placeholder="策略名称" clearable style="width: 180px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item label="文案">
        <el-input v-model="filterText" placeholder="文案关键字" clearable style="width: 180px" @keyup.enter="loadList" />
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
      <el-table-column prop="strategyDesc" label="描述" min-width="150" show-overflow-tooltip />
      <el-table-column prop="textContents" label="文案" min-width="260" show-overflow-tooltip />
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

    <el-dialog v-model="editorVisible" :title="editorMode === 'create' ? '新建走马灯策略' : '编辑走马灯策略'" width="560px">
      <el-form :model="editor" label-width="90px">
        <el-form-item label="策略名称" required>
          <el-input v-model="editor.strategyName" maxlength="60" />
        </el-form-item>
        <el-form-item label="策略说明">
          <el-input v-model="editor.strategyDesc" maxlength="200" />
        </el-form-item>
        <el-form-item label="文案" required>
          <el-input v-model="editor.textContents" type="textarea" :rows="5" maxlength="1000" show-word-limit placeholder="请输入文案内容" />
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
  textContents: string | null
  ctorName: string | null
  ctime: string | null
  utorName: string | null
  utime: string | null
}

interface StrategyDetail {
  strategyId: number
  strategyName: string
  strategyDesc: string
  textContents: string | null
}

const rows = ref<StrategyRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterName = ref('')
const filterText = ref('')
const selectedRows = ref<StrategyRow[]>([])

const editorVisible = ref(false)
const editorMode = ref<'create' | 'edit'>('create')
const saving = ref(false)
const editor = ref<{ strategyId?: number; strategyName: string; strategyDesc: string; textContents: string }>({
  strategyName: '',
  strategyDesc: '',
  textContents: '',
})

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
    if (filterName.value) params.set('searchStVmName', filterName.value)
    if (filterText.value) params.set('searchStVmText', filterText.value)
    const res = await api.get<{ total: number; data: StrategyRow[] }>(`/vms/api/stVmText/list?${params.toString()}`)
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
  editor.value = { strategyName: '', strategyDesc: '', textContents: '' }
  editorVisible.value = true
}

async function openEdit(row: StrategyRow) {
  editorMode.value = 'edit'
  editor.value = { strategyId: row.strategyId, strategyName: row.strategyName, strategyDesc: row.strategyDesc, textContents: row.textContents || '' }
  editorVisible.value = true
  try {
    const detail = await api.get<StrategyDetail>(`/vms/api/stVmText/${row.strategyId}`)
    editor.value = {
      strategyId: detail.strategyId,
      strategyName: detail.strategyName,
      strategyDesc: detail.strategyDesc,
      textContents: detail.textContents || '',
    }
  } catch (e) {
    ElMessage.error('加载策略详情失败')
  }
}

async function save() {
  if (!editor.value.strategyName || !editor.value.textContents) {
    ElMessage.warning('请填写策略名称和文案')
    return
  }
  saving.value = true
  try {
    const payload: Record<string, any> = {
      strategyName: editor.value.strategyName,
      strategyDesc: editor.value.strategyDesc,
      textContents: editor.value.textContents,
    }
    let path = '/vms/api/stVmText/create'
    if (editorMode.value === 'edit') {
      payload.strategyId = editor.value.strategyId
      path = '/vms/api/stVmText/edit'
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
    const res = await api.post<any>('/vms/api/stVmText/delete', {
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

onMounted(loadList)
</script>

<style scoped>
.text-strategy h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.time { color: #95a5a6; font-size: 12px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
