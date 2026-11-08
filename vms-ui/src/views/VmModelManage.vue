<template>
  <div class="model-manage">
    <h1>型号货道</h1>
    <p class="hint">管理售货机型号及其货道配置。</p>

    <div class="action-bar">
      <el-button type="primary" @click="showDialog()">新增型号</el-button>
    </div>

    <!-- 筛选 -->
    <el-form :inline="true" class="filter-form">
      <el-form-item label="型号名称">
        <el-input v-model="searchName" placeholder="搜索型号" clearable @keyup.enter="loadModels" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadModels">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="models" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="modelName" label="型号名称" min-width="180" />
      <el-table-column prop="modelDesc" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="modelType" label="型号类型" width="100" />
      <el-table-column prop="channelType" label="货道类型" width="100" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="showDialog(scope.row)">编辑</el-button>
          <el-button type="success" link @click="showChannels(scope.row)">货道</el-button>
          <el-button type="danger" link @click="deleteModel(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      layout="total, prev, pager, next"
      @current-change="loadModels"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 型号编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑型号' : '新增型号'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="型号名称" required>
          <el-input v-model="form.modelName" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.modelDesc" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="型号类型">
          <el-input-number v-model="form.modelType" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="货道类型">
          <el-input-number v-model="form.channelType" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 货道查看对话框 -->
    <el-dialog v-model="channelsVisible" :title="`货道配置 — ${currentModelName}`" width="700px">
      <el-table :data="channels" v-loading="channelsLoading" border size="small" style="width: 100%">
        <el-table-column prop="channelCode" label="货道编号" width="100" />
        <el-table-column prop="channelWidth" label="宽" width="80" />
        <el-table-column prop="channelHeight" label="高" width="80" />
        <el-table-column prop="channelLength" label="深" width="80" />
        <el-table-column prop="acceptWidthMax" label="最大宽度" width="100" />
        <el-table-column prop="isFixedCapacity" label="固定容量" width="100">
          <template #default="scope">{{ scope.row.isFixedCapacity === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column prop="fixedCapacity" label="容量" width="80" />
        <el-table-column prop="displayMode" label="展示模式" width="100" />
      </el-table>
      <template #footer>
        <el-button @click="channelsVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface Model {
  id: number
  modelName: string
  modelDesc: string
  modelType: number
  channelType: number
}

interface Channel {
  id: number
  channelCode: string
  channelWidth: number
  channelHeight: number
  channelLength: number
  channelType: number
  acceptWidthMax: number
  isFixedCapacity: number
  fixedCapacity: number
  displayMode: number
}

const models = ref<Model[]>([])
const loading = ref(false)
const searchName = ref('')
const pagination = ref({ page: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const form = ref({
  id: undefined as number | undefined,
  modelName: '',
  modelDesc: '',
  modelType: 0,
  channelType: 0,
})
const submitting = ref(false)

const channelsVisible = ref(false)
const channelsLoading = ref(false)
const channels = ref<Channel[]>([])
const currentModelName = ref('')

async function loadModels() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.append('page', pagination.value.page.toString())
    params.append('pageSize', pagination.value.pageSize.toString())
    if (searchName.value) params.append('name', searchName.value)
    const res = await api.get<{ total: number; data: Model[] }>(`/vms/api/vmModel/list?${params}`)
    models.value = res.data
    pagination.value.total = res.total
  } catch (e: any) {
    ElMessage.error('加载型号失败')
  } finally {
    loading.value = false
  }
}

function showDialog(row?: Model) {
  if (row) {
    form.value = {
      id: row.id,
      modelName: row.modelName,
      modelDesc: row.modelDesc || '',
      modelType: row.modelType || 0,
      channelType: row.channelType || 0,
    }
  } else {
    form.value = { id: undefined, modelName: '', modelDesc: '', modelType: 0, channelType: 0 }
  }
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.value.modelName) {
    ElMessage.warning('请输入型号名称')
    return
  }
  submitting.value = true
  try {
    const formData = new FormData()
    formData.append('modelName', form.value.modelName)
    if (form.value.modelDesc) formData.append('modelDesc', form.value.modelDesc)
    formData.append('modelType', form.value.modelType.toString())
    formData.append('channelType', form.value.channelType.toString())

    const url = form.value.id ? `/vms/api/vmModel/${form.value.id}/update` : '/vms/api/vmModel/create'
    const res = await api.post<{ success: boolean; message: string }>(url, formData)
    if (res.success) {
      ElMessage.success(res.message)
      dialogVisible.value = false
      loadModels()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

async function showChannels(row: Model) {
  currentModelName.value = row.modelName
  channelsVisible.value = true
  channelsLoading.value = true
  try {
    channels.value = await api.get<Channel[]>(`/vms/api/vmModel/${row.id}/channels`)
  } catch (e: any) {
    ElMessage.error('加载货道失败')
  } finally {
    channelsLoading.value = false
  }
}

async function deleteModel(row: Model) {
  try {
    await ElMessageBox.confirm(`确定删除型号「${row.modelName}」吗？`, '确认删除', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<{ success: boolean; message: string }>(
      `/vms/api/vmModel/${row.id}/delete`,
      new FormData(),
    )
    if (res.success) {
      ElMessage.success(res.message)
      loadModels()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => loadModels())
</script>

<style scoped>
.model-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.action-bar { margin-bottom: 16px; }
.filter-form { margin-bottom: 16px; padding: 16px; background: white; border-radius: 4px; }
</style>
