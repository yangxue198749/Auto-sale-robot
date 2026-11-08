<template>
  <div class="vm-manage">
    <h1>售货机管理</h1>
    <p class="hint">管理售货机设备（编号、点位、型号、状态、销售控制）。</p>

    <div class="action-bar">
      <el-button type="primary" @click="showDialog()">新增设备</el-button>
    </div>

    <!-- 筛选 -->
    <el-form :inline="true" class="filter-form">
      <el-form-item label="搜索">
        <el-select v-model="filters.filedType" placeholder="搜索字段" style="width: 120px">
          <el-option label="设备编号" :value="1" />
          <el-option label="设备描述" :value="2" />
        </el-select>
        <el-input v-model="filters.filedValue" placeholder="搜索值" style="width: 200px; margin-left: 10px" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadVms">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="vms" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="innerCode" label="设备编号" width="140" />
      <el-table-column prop="vmDesc" label="描述" min-width="150" show-overflow-tooltip />
      <el-table-column prop="nodeName" label="点位" min-width="150" show-overflow-tooltip />
      <el-table-column prop="companyName" label="所属公司" width="140" show-overflow-tooltip />
      <el-table-column prop="vmStatus" label="状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.vmStatus === 1 ? 'success' : 'info'" size="small">
            {{ scope.row.vmStatus === 1 ? '在售' : '停售/撤机' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="stockStatus" label="库存状态" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="showDialog(scope.row)">编辑</el-button>
          <el-button type="warning" link @click="changeSale(scope.row)">销售控制</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadVms"
      @current-change="loadVms"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑设备' : '新增设备'" width="560px">
      <el-form :model="form" label-width="100px">
        <template v-if="!form.id">
          <el-form-item label="点位" required>
            <el-select v-model="form.nodeId" filterable remote :remote-method="searchNodes" :loading="nodeLoading" placeholder="搜索点位名称">
              <el-option v-for="n in nodeOptions" :key="n.id" :label="`${n.nodeName} - ${n.nodeAddress || ''}`" :value="n.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="型号" required>
            <el-select v-model="form.vmModelId" filterable placeholder="选择型号">
              <el-option v-for="m in modelOptions" :key="m.id" :label="m.modelName" :value="m.id" />
            </el-select>
          </el-form-item>
        </template>
        <el-form-item label="设备描述">
          <el-input v-model="form.vmDesc" placeholder="如：1号门店饮料机" />
        </el-form-item>
        <el-form-item v-if="form.id" label="设备编号">
          <el-input :value="form.innerCode" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface Vm {
  id: number
  innerCode: string
  vmDesc: string
  vmStatus: number
  stockStatus: number
  vmModelId: number
  nodeId: number
  nodeName: string
  companyName: string
}

interface NodeOption {
  id: number
  nodeName: string
  nodeAddress: string
}

interface ModelOption {
  id: number
  modelName: string
}

const vms = ref<Vm[]>([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 10, total: 0 })

const filters = ref({
  filedType: 1,
  filedValue: '',
})

const dialogVisible = ref(false)
const form = ref({
  id: undefined as number | undefined,
  innerCode: '',
  nodeId: undefined as number | undefined,
  vmModelId: undefined as number | undefined,
  vmDesc: '',
})
const submitting = ref(false)

const nodeOptions = ref<NodeOption[]>([])
const nodeLoading = ref(false)
const modelOptions = ref<ModelOption[]>([])

async function loadVms() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.append('page', pagination.value.page.toString())
    params.append('pageSize', pagination.value.pageSize.toString())
    if (filters.value.filedValue) {
      params.append('filedType', filters.value.filedType.toString())
      params.append('filedValue', filters.value.filedValue)
    }
    const res = await api.get<{ total: number; data: Vm[] }>(`/vms/api/mtVm/list?${params}`)
    vms.value = res.data
    pagination.value.total = res.total
  } catch (e: any) {
    ElMessage.error('加载设备失败')
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.value = { filedType: 1, filedValue: '' }
  pagination.value.page = 1
  loadVms()
}

async function searchNodes(query: string) {
  if (!query) {
    nodeOptions.value = []
    return
  }
  nodeLoading.value = true
  try {
    nodeOptions.value = await api.get<NodeOption[]>(
      `/vms/api/mtNode/list?page=1&pageSize=50&searchType=nodeName&searchValue=${encodeURIComponent(query)}`,
    )
  } catch (e) {
    ElMessage.error('搜索点位失败')
  } finally {
    nodeLoading.value = false
  }
}

async function loadModelOptions() {
  try {
    const res = await api.get<{ total: number; data: ModelOption[] }>('/vms/api/vmModel/list?page=1&pageSize=100')
    modelOptions.value = res.data
  } catch (e) {
    // ignore
  }
}

function showDialog(row?: Vm) {
  if (row) {
    form.value = {
      id: row.id,
      innerCode: row.innerCode,
      nodeId: row.nodeId,
      vmModelId: row.vmModelId,
      vmDesc: row.vmDesc || '',
    }
  } else {
    form.value = { id: undefined, innerCode: '', nodeId: undefined, vmModelId: undefined, vmDesc: '' }
    if (modelOptions.value.length === 0) loadModelOptions()
  }
  dialogVisible.value = true
}

async function submitForm() {
  submitting.value = true
  try {
    const formData = new FormData()
    if (form.value.id) {
      if (form.value.vmDesc) formData.append('vmDesc', form.value.vmDesc)
      const res = await api.post<{ success: boolean; message: string }>(
        `/vms/api/mtVm/${form.value.id}/update`,
        formData,
      )
      if (res.success) {
        ElMessage.success(res.message)
        dialogVisible.value = false
        loadVms()
      } else {
        ElMessage.error(res.message)
      }
    } else {
      if (form.value.nodeId === undefined) {
        ElMessage.warning('请选择点位')
        return
      }
      if (form.value.vmModelId === undefined) {
        ElMessage.warning('请选择型号')
        return
      }
      formData.append('nodeId', form.value.nodeId.toString())
      formData.append('vmModelId', form.value.vmModelId.toString())
      if (form.value.vmDesc) formData.append('vmDesc', form.value.vmDesc)

      const res = await api.post<{ success: boolean; message: string }>('/vms/api/mtVm/create', formData)
      if (res.success) {
        ElMessage.success(res.message)
        dialogVisible.value = false
        loadVms()
      } else {
        ElMessage.error(res.message)
      }
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

async function changeSale(row: Vm) {
  try {
    await ElMessageBox.confirm(
      `确定切换设备「${row.innerCode}」的销售状态吗？（当前：${row.vmStatus === 1 ? '在售' : '停售'}）`,
      '销售控制',
      { type: 'warning' },
    )
  } catch {
    return
  }
  try {
    const res = await api.post<{ success: boolean; message: string }>(
      `/vms/api/mtVm/${row.id}/changeSale`,
      new FormData(),
    )
    if (res.success) {
      ElMessage.success(res.message)
      loadVms()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('切换失败')
  }
}

onMounted(() => loadVms())
</script>

<style scoped>
.vm-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.action-bar { margin-bottom: 16px; }
.filter-form { margin-bottom: 16px; padding: 16px; background: white; border-radius: 4px; }
</style>
