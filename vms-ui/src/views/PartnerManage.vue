<template>
  <div class="partner-manage">
    <h1>合伙人信息</h1>
    <p class="hint">维护合伙人档案，可关联点位分成。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="筛选">
        <el-select v-model="filterType" style="width: 110px">
          <el-option label="合伙人名称" value="1" />
          <el-option label="负责人" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="filterText" placeholder="关键字" clearable style="width: 160px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openCreate">新建合伙人</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="deleteSelected">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="partnerName" label="合伙人名称" min-width="160" show-overflow-tooltip />
      <el-table-column label="类型" width="100">
        <template #default="scope">{{ dictName(options, scope.row.partnerType) }}</template>
      </el-table-column>
      <el-table-column prop="managerName" label="负责人" width="100" />
      <el-table-column prop="managerPhone" label="电话" width="130" />
      <el-table-column prop="partnerEmail" label="邮箱" min-width="140" show-overflow-tooltip />
      <el-table-column prop="partnerAddress" label="地址" min-width="160" show-overflow-tooltip />
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

    <el-dialog v-model="editorVisible" :title="editorMode === 'create' ? '新建合伙人' : '编辑合伙人'" width="520px">
      <el-form :model="editor" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="editor.partnerName" maxlength="80" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editor.partnerType" placeholder="选择类型" style="width: 200px">
            <el-option v-for="d in options" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="editor.managerName" maxlength="40" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="editor.managerPhone" maxlength="20" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editor.partnerEmail" maxlength="60" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="editor.partnerAddress" maxlength="200" />
        </el-form-item>
        <el-form-item label="税号">
          <el-input v-model="editor.taxCode" maxlength="40" />
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

interface PartnerRow {
  id: number
  partnerName: string
  partnerType: number | null
  managerName: string | null
  managerPhone: string | null
  partnerEmail: string | null
  partnerAddress: string | null
  taxCode: string | null
}

interface DictOption {
  id: number
  name: string
}

const rows = ref<PartnerRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterType = ref('1')
const filterText = ref('')
const selectedRows = ref<PartnerRow[]>([])
const options = ref<DictOption[]>([])

const editorVisible = ref(false)
const editorMode = ref<'create' | 'edit'>('create')
const saving = ref(false)
const editor = ref<{
  id?: number
  partnerName: string
  partnerType: number | undefined
  managerName: string
  managerPhone: string
  partnerEmail: string
  partnerAddress: string
  taxCode: string
}>(emptyEditor())

function emptyEditor() {
  return {
    partnerName: '',
    partnerType: undefined,
    managerName: '',
    managerPhone: '',
    partnerEmail: '',
    partnerAddress: '',
    taxCode: '',
  }
}

function dictName(list: DictOption[], id: number | null) {
  if (id == null) return '-'
  return list.find((d) => d.id === id)?.name || String(id)
}

async function loadOptions() {
  try {
    options.value = await api.get<DictOption[]>('/vms/api/partner/options')
  } catch (e) {
    // 字典加载失败不阻塞
  }
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterText.value) {
      params.set('searchType', filterType.value)
      params.set('searchName', filterText.value)
    }
    const res = await api.get<{ total: number; data: PartnerRow[] }>(`/vms/api/partner/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载合伙人列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: PartnerRow[]) {
  selectedRows.value = selection
}

function openCreate() {
  editorMode.value = 'create'
  editor.value = emptyEditor()
  editorVisible.value = true
}

function openEdit(row: PartnerRow) {
  editorMode.value = 'edit'
  editor.value = {
    id: row.id,
    partnerName: row.partnerName,
    partnerType: row.partnerType ?? undefined,
    managerName: row.managerName || '',
    managerPhone: row.managerPhone || '',
    partnerEmail: row.partnerEmail || '',
    partnerAddress: row.partnerAddress || '',
    taxCode: row.taxCode || '',
  }
  editorVisible.value = true
}

async function save() {
  if (!editor.value.partnerName) {
    ElMessage.warning('请填写名称')
    return
  }
  saving.value = true
  try {
    const payload: Record<string, any> = { ...editor.value }
    let path = '/vms/api/partner/create'
    if (editorMode.value === 'edit') {
      path = `/vms/api/partner/${editor.value.id}/update`
      delete payload.id
    }
    const res = await api.post<any>(path, payload, { form: true })
    handleResult(res, '保存成功')
    if (res?.code === 0 || res?.type === 'success') {
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
    await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个合伙人？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/partner/delete', {
      ids: selectedRows.value.map((r) => r.id).join(','),
    }, { form: true })
    handleResult(res, '删除成功')
    loadList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function handleResult(res: any, successMessage: string) {
  if (res?.code === 0 || res?.type === 'success' || res?.success) {
    ElMessage.success(res?.message || successMessage)
  } else {
    ElMessage.error(res?.message || '操作失败')
  }
}

onMounted(() => {
  loadOptions()
  loadList()
})
</script>

<style scoped>
.partner-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
