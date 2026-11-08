<template>
  <div class="theme-strategy">
    <h1>皮肤策略</h1>
    <p class="hint">上传皮肤包（zip）与预览图（png）作为主题，可在设备皮肤配置中绑定到售货机。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="皮肤名称">
        <el-input v-model="filterName" placeholder="皮肤名称" clearable style="width: 180px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openCreate">上传皮肤</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="deleteSelected">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column label="预览" width="110">
        <template #default="scope">
          <img :src="scope.row.previewFullUrl" class="preview-img" @click="openPreview(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column prop="themeName" label="皮肤名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="themeDesc" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column label="大小" width="100">
        <template #default="scope">{{ formatSize(scope.row.themeSize) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openPreview(scope.row)">预览</el-button>
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

    <!-- 上传 -->
    <el-dialog v-model="createVisible" title="上传皮肤" width="520px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="皮肤名称" required>
          <el-input v-model="createForm.themeName" maxlength="60" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.themeDesc" type="textarea" :rows="2" maxlength="200" />
        </el-form-item>
        <el-form-item label="皮肤包">
          <input type="file" accept=".zip" class="file-input" @change="onZipChange" />
          <div class="file-hint">zip 格式，最大 {{ maxZipMb }}MB</div>
        </el-form-item>
        <el-form-item label="预览图">
          <input type="file" accept=".png" class="file-input" @change="onPreviewChange" />
          <div class="file-hint">png 格式，最大 {{ maxPreviewMb }}MB</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="upload">上传</el-button>
      </template>
    </el-dialog>

    <!-- 预览 -->
    <el-dialog v-model="previewVisible" title="皮肤预览" width="420px">
      <div class="preview-box">
        <img v-if="previewRow" :src="previewRow.previewFullUrl" class="preview-big" />
        <p class="preview-name">{{ previewRow?.themeName }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface ThemeRow {
  id: number
  themeName: string
  themeDesc: string
  themeUrl: string
  themeSize: number | null
  previewUrls: string
  previewFullUrl: string
}

const rows = ref<ThemeRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterName = ref('')
const selectedRows = ref<ThemeRow[]>([])

const createVisible = ref(false)
const uploading = ref(false)
const createForm = ref({ themeName: '', themeDesc: '' })
const zipFile = ref<File | null>(null)
const previewFile = ref<File | null>(null)
const maxZipMb = ref(10)
const maxPreviewMb = ref(3)

const previewVisible = ref(false)
const previewRow = ref<ThemeRow | null>(null)

function formatSize(size: number | null) {
  if (!size) return '-'
  if (size > 1024 * 1024) return `${(size / 1024 / 1024).toFixed(1)} MB`
  return `${(size / 1024).toFixed(0)} KB`
}

function onZipChange(event: Event) {
  const input = event.target as HTMLInputElement
  zipFile.value = input.files?.[0] || null
}

function onPreviewChange(event: Event) {
  const input = event.target as HTMLInputElement
  previewFile.value = input.files?.[0] || null
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterName.value) params.set('themeName', filterName.value)
    const res = await api.get<{ total: number; data: ThemeRow[] }>(`/vms/api/theme/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载皮肤列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: ThemeRow[]) {
  selectedRows.value = selection
}

function openCreate() {
  createForm.value = { themeName: '', themeDesc: '' }
  zipFile.value = null
  previewFile.value = null
  createVisible.value = true
}

async function upload() {
  if (!createForm.value.themeName) {
    ElMessage.warning('请填写皮肤名称')
    return
  }
  if (!zipFile.value) {
    ElMessage.warning('请选择皮肤包（zip）')
    return
  }
  if (!previewFile.value) {
    ElMessage.warning('请选择预览图（png）')
    return
  }
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('themeName', createForm.value.themeName)
    formData.append('themeDesc', createForm.value.themeDesc)
    formData.append('themeZip', zipFile.value)
    formData.append('themePreview', previewFile.value)
    const res = await api.upload<any>('/vms/api/theme/create', formData)
    handleResult(res, '上传成功')
    if (res?.success || res?.type === 'success') {
      createVisible.value = false
      loadList()
    }
  } catch (e) {
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

async function deleteSelected() {
  if (!selectedRows.value.length) return
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个皮肤？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/theme/delete', {
      ids: selectedRows.value.map((r) => r.id).join(','),
    }, { form: true })
    handleResult(res, '删除成功')
    loadList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function openPreview(row: ThemeRow) {
  previewRow.value = row
  previewVisible.value = true
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
.theme-strategy h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.preview-img { width: 84px; height: 48px; object-fit: contain; border: 1px solid #eee; cursor: pointer; }
.preview-box { text-align: center; }
.preview-big { max-width: 100%; max-height: 480px; }
.preview-name { margin-top: 8px; color: #606266; }
.file-input { font-size: 12px; }
.file-hint { color: #95a5a6; font-size: 12px; margin-top: 4px; }
</style>
