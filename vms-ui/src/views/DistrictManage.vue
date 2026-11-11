<template>
  <div class="district-manage">
    <h1>商圈信息</h1>
    <p class="hint">维护商圈字典，点位管理时可关联。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item>
        <el-button type="success" @click="openCreate">新建商圈</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="deleteSelected">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="name" label="商圈名称" min-width="200" />
      <el-table-column label="创建时间" min-width="160">
        <template #default="scope">{{ formatTime(scope.row.ctime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="onPageChange" />

    <el-dialog v-model="editorVisible" :title="editorId ? '编辑商圈' : '新建商圈'" width="420px">
      <el-input v-model="editorName" placeholder="商圈名称" maxlength="60" />
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

interface DistrictRow {
  id: number
  name: string
  ctime: string | null
}

const rows = ref<DistrictRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const selectedRows = ref<DistrictRow[]>([])

const editorVisible = ref(false)
const editorId = ref<number | null>(null)
const editorName = ref('')
const saving = ref(false)

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
    const res = await api.get<{ total: number; data: DistrictRow[] }>(`/vms/api/businessDistrict/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载商圈失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: DistrictRow[]) {
  selectedRows.value = selection
}

function openCreate() {
  editorId.value = null
  editorName.value = ''
  editorVisible.value = true
}

function openEdit(row: DistrictRow) {
  editorId.value = row.id
  editorName.value = row.name
  editorVisible.value = true
}

async function save() {
  if (!editorName.value) {
    ElMessage.warning('请填写商圈名称')
    return
  }
  saving.value = true
  try {
    let res
    if (editorId.value) {
      res = await api.post<any>('/vms/api/businessDistrict/update', { ids: editorId.value, name: editorName.value }, { form: true })
    } else {
      res = await api.post<any>('/vms/api/businessDistrict/create', { name: editorName.value }, { form: true })
    }
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
    await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个商圈？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/businessDistrict/delete', {
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
.district-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
