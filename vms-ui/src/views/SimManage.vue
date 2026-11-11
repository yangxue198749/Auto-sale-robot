<template>
  <div class="sim-manage">
    <h1>SIM 卡信息</h1>
    <p class="hint">管理物联网 SIM 卡（运营商/号码/卡号），设备绑定后显示使用状态。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="筛选">
        <el-select v-model="filterType" style="width: 110px">
          <el-option label="手机号" value="1" />
          <el-option label="卡号" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="filterText" placeholder="关键字" clearable style="width: 150px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item label="运营商">
        <el-select v-model="filterProvider" clearable placeholder="全部" style="width: 100px">
          <el-option label="移动" :value="1" />
          <el-option label="联通" :value="2" />
          <el-option label="电信" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filterUse" clearable placeholder="全部" style="width: 100px">
          <el-option label="使用中" value="1" />
          <el-option label="未使用" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
        <el-button type="success" @click="openCreate">新建 SIM</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="deleteSelected">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" :selectable="(row: SimRow) => !row.used" />
      <el-table-column prop="simPhone" label="手机号" min-width="130" />
      <el-table-column prop="simNumber" label="卡号" min-width="160" show-overflow-tooltip />
      <el-table-column label="运营商" width="90">
        <template #default="scope">{{ providerText(scope.row.serviceProvider) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.used ? 'success' : 'info'" size="small">{{ scope.row.used ? '使用中' : '未使用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="vmInnerCode" label="绑定设备" min-width="120" />
      <el-table-column label="操作" width="80" fixed="right">
        <template #default="scope">
          <el-button link type="primary" :disabled="scope.row.used" @click="openEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="onPageChange" />

    <el-dialog v-model="editorVisible" :title="editorId ? '编辑 SIM' : '新建 SIM'" width="440px">
      <el-form :model="editor" label-width="80px">
        <el-form-item label="手机号" required><el-input v-model="editor.simPhone" maxlength="20" /></el-form-item>
        <el-form-item label="卡号" required><el-input v-model="editor.simNumber" maxlength="40" /></el-form-item>
        <el-form-item label="运营商" required>
          <el-select v-model="editor.serviceProvider" style="width: 160px">
            <el-option label="移动" :value="1" />
            <el-option label="联通" :value="2" />
            <el-option label="电信" :value="3" />
          </el-select>
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

interface SimRow {
  id: number
  simPhone: string
  simNumber: string
  serviceProvider: number | null
  used: boolean
  vmInnerCode: string | null
}

const rows = ref<SimRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterType = ref('1')
const filterText = ref('')
const filterProvider = ref<number | undefined>(undefined)
const filterUse = ref<string | undefined>(undefined)
const selectedRows = ref<SimRow[]>([])

const editorVisible = ref(false)
const editorId = ref<number | null>(null)
const saving = ref(false)
const editor = ref<{ simPhone: string; simNumber: string; serviceProvider: number | undefined }>({
  simPhone: '', simNumber: '', serviceProvider: undefined,
})

function providerText(p: number | null) {
  if (p === 1) return '移动'
  if (p === 2) return '联通'
  if (p === 3) return '电信'
  return '-'
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterText.value) {
      params.set('selectType', filterType.value)
      params.set('selectText', filterText.value)
    }
    if (filterProvider.value !== undefined) params.set('serviceProvider', String(filterProvider.value))
    if (filterUse.value) params.set('simUse', filterUse.value)
    const res = await api.get<{ total: number; data: SimRow[] }>(`/vms/api/sim/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载 SIM 列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: SimRow[]) {
  selectedRows.value = selection
}

function openCreate() {
  editorId.value = null
  editor.value = { simPhone: '', simNumber: '', serviceProvider: undefined }
  editorVisible.value = true
}

function openEdit(row: SimRow) {
  editorId.value = row.id
  editor.value = { simPhone: row.simPhone, simNumber: row.simNumber, serviceProvider: row.serviceProvider ?? undefined }
  editorVisible.value = true
}

async function save() {
  const f = editor.value
  if (!f.simPhone || !f.simNumber || f.serviceProvider === undefined) {
    ElMessage.warning('请填写完整')
    return
  }
  saving.value = true
  try {
    let path = '/vms/api/sim/create'
    const payload: Record<string, any> = { ...f }
    if (editorId.value) {
      path = '/vms/api/sim/edit'
      payload.simId = editorId.value
    }
    const res = await api.post<any>(path, payload, { form: true })
    if (res?.code === 0) {
      ElMessage.success(res.message || '保存成功')
      editorVisible.value = false
      loadList()
    } else {
      ElMessage.error(res?.message || '保存失败')
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
    await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 张 SIM 卡？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/sim/delete', {
      ids: selectedRows.value.map((r) => r.id).join(','),
    }, { form: true })
    if (res?.code === 0) {
      ElMessage.success('删除成功')
      loadList()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.sim-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
