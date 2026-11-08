<template>
  <div class="settings-manage">
    <h1>系统设置</h1>
    <p class="hint">维护系统配置项（按类型分组；refund_type / upload_type 分组下的配置可编辑）。</p>

    <div class="layout">
      <el-card shadow="never" class="type-panel">
        <template #header>配置类型</template>
        <el-input v-model="typeSearch" placeholder="搜索类型" clearable size="small" style="margin-bottom: 8px" @keyup.enter="loadTypes" />
        <div
          v-for="t in types"
          :key="t.id"
          class="type-item"
          :class="{ active: currentType?.id === t.id }"
          @click="selectType(t)"
        >
          <span>{{ t.name || t.key }}</span>
          <span class="count">{{ t.childCount }}</span>
        </div>
        <div v-if="!types.length" class="empty-tip">暂无类型</div>
      </el-card>

      <el-card shadow="never" class="setting-panel">
        <template #header>
          <span>{{ currentType ? `${currentType.name || currentType.key} 配置项` : '配置项' }}</span>
          <el-button type="primary" size="small" style="float: right" :disabled="!currentType" @click="openCreate">新增配置</el-button>
        </template>
        <el-table :data="rows" v-loading="loading" size="small" border style="width: 100%">
          <el-table-column prop="name" label="名称" min-width="140" show-overflow-tooltip />
          <el-table-column prop="key" label="Key" min-width="180" show-overflow-tooltip />
          <el-table-column prop="value" label="值" min-width="160" show-overflow-tooltip />
          <el-table-column label="更新时间" min-width="150">
            <template #default="scope">{{ formatTime(scope.row.utime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="openEdit(scope.row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="deleteRow(scope.row)">删除</el-button>
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
      </el-card>
    </div>

    <el-dialog v-model="editorVisible" :title="editor.settingId ? '编辑配置' : '新增配置'" width="480px">
      <el-form :model="editor" label-width="70px">
        <el-form-item label="名称">
          <el-input v-model="editor.name" maxlength="60" />
        </el-form-item>
        <el-form-item label="Key" required>
          <el-input v-model="editor.key" maxlength="80" placeholder="英文 key" />
        </el-form-item>
        <el-form-item label="值">
          <el-input v-model="editor.value" maxlength="200" />
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

interface TypeRow {
  id: number
  name: string | null
  key: string
  childCount: number
}

interface SettingRow {
  id: number
  name: string | null
  key: string
  value: string | null
  utime: string | null
}

const types = ref<TypeRow[]>([])
const typeSearch = ref('')
const currentType = ref<TypeRow | null>(null)

const rows = ref<SettingRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const editorVisible = ref(false)
const saving = ref(false)
const editor = ref<{ settingId?: number; name: string; key: string; value: string }>({ name: '', key: '', value: '' })

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

async function loadTypes() {
  try {
    const query = typeSearch.value ? `?searchValue=${encodeURIComponent(typeSearch.value)}` : ''
    types.value = await api.get<TypeRow[]>(`/vms/api/settings/types${query}`)
    if (!currentType.value && types.value.length) {
      selectType(types.value[0])
    }
  } catch (e) {
    ElMessage.error('加载配置类型失败')
  }
}

async function selectType(t: TypeRow) {
  currentType.value = t
  page.value = 1
  await loadSettings()
}

async function loadSettings() {
  if (!currentType.value) return
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    const res = await api.get<{ total: number; data: SettingRow[] }>(
      `/vms/api/settings/${currentType.value.id}/settings?${params.toString()}`,
    )
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载配置项失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadSettings()
}

function openCreate() {
  editor.value = { name: '', key: '', value: '' }
  editorVisible.value = true
}

function openEdit(row: SettingRow) {
  editor.value = { settingId: row.id, name: row.name || '', key: row.key, value: row.value || '' }
  editorVisible.value = true
}

async function save() {
  if (!editor.value.key) {
    ElMessage.warning('请填写 Key')
    return
  }
  if (!currentType.value) return
  saving.value = true
  try {
    const payload: Record<string, any> = {
      pid: currentType.value.id,
      name: editor.value.name,
      key: editor.value.key,
      value: editor.value.value,
    }
    if (editor.value.settingId) payload.settingId = editor.value.settingId
    const res = await api.post<any>('/vms/api/settings/save', payload, { form: true })
    handleResult(res, '保存成功')
    if (res?.code === 0 || res?.type === 'success') {
      editorVisible.value = false
      loadSettings()
      loadTypes()
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function deleteRow(row: SettingRow) {
  try {
    await ElMessageBox.confirm(`确定删除配置 ${row.key}？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/settings/delete', { ids: String(row.id) }, { form: true })
    handleResult(res, '删除成功')
    loadSettings()
    loadTypes()
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

onMounted(loadTypes)
</script>

<style scoped>
.settings-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.layout { display: flex; gap: 16px; align-items: flex-start; }
.type-panel { width: 260px; flex-shrink: 0; }
.setting-panel { flex: 1; }
.type-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 10px;
  border-radius: 4px;
  cursor: pointer;
  color: #303133;
}
.type-item:hover { background: #f5f7fa; }
.type-item.active { background: #ecf5ff; color: #409eff; }
.type-item .count { color: #909399; font-size: 12px; }
.empty-tip { color: #95a5a6; text-align: center; padding: 20px 0; }
.pager { margin-top: 12px; justify-content: flex-end; }
</style>
