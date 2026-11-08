<template>
  <div class="node-manage">
    <h1>点位管理</h1>
    <p class="hint">管理售货机点位（地址、区域、负责人配置）。</p>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="showDialog()">新增点位</el-button>
    </div>

    <!-- 筛选 -->
    <el-form :inline="true" class="filter-form">
      <el-form-item label="搜索">
        <el-select v-model="filters.searchType" placeholder="搜索字段" style="width: 120px">
          <el-option label="点位名称" value="nodeName" />
          <el-option label="点位地址" value="nodeAddress" />
        </el-select>
        <el-input v-model="filters.searchValue" placeholder="搜索值" style="width: 200px; margin-left: 10px" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadNodes">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 列表 -->
    <el-table :data="nodes" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="nodeName" label="点位名称" min-width="150" />
      <el-table-column prop="nodeAddress" label="地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="deliveryName" label="配送员" width="110" />
      <el-table-column prop="repairName" label="维修员" width="110" />
      <el-table-column prop="partnerName" label="合作伙伴" width="130" />
      <el-table-column prop="propertyManager" label="物业联系人" width="120" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="showDialog(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="deleteNode(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadNodes"
      @current-change="loadNodes"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑点位' : '新增点位'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="点位名称" required>
          <el-input v-model="form.nodeName" />
        </el-form-item>
        <el-form-item label="点位地址">
          <el-input v-model="form.nodeAddress" />
        </el-form-item>
        <el-form-item label="配送员">
          <el-select v-model="form.deliveryId" filterable clearable placeholder="选择配送员">
            <el-option
              v-for="staff in staffOptions"
              :key="staff.id"
              :label="`${staff.userName} (${staff.userAccount})`"
              :value="staff.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="维修员">
          <el-select v-model="form.repairId" filterable clearable placeholder="选择维修员">
            <el-option
              v-for="staff in staffOptions"
              :key="staff.id"
              :label="`${staff.userName} (${staff.userAccount})`"
              :value="staff.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="物业名称">
          <el-input v-model="form.propertyName" />
        </el-form-item>
        <el-form-item label="物业联系人">
          <el-input v-model="form.propertyManager" />
        </el-form-item>
        <el-form-item label="物业电话">
          <el-input v-model="form.propertyPhone" />
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

interface Node {
  id: number
  nodeName: string
  nodeAddress: string
  provinceId: number
  cityId: number
  areaId: number
  businessDistrictId: number
  partnerId: number
  partnerName: string
  deliveryId: number
  deliveryName: string
  repairId: number
  repairName: string
  propertyName: string
  propertyManager: string
  propertyPhone: string
}

interface Staff {
  id: number
  userName: string
  userAccount: string
}

const nodes = ref<Node[]>([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 10, total: 0 })

const filters = ref({
  searchType: 'nodeName',
  searchValue: '',
})

const staffOptions = ref<Staff[]>([])

const dialogVisible = ref(false)
const form = ref({
  id: undefined as number | undefined,
  nodeName: '',
  nodeAddress: '',
  deliveryId: undefined as number | undefined,
  repairId: undefined as number | undefined,
  propertyName: '',
  propertyManager: '',
  propertyPhone: '',
})
const submitting = ref(false)

async function loadNodes() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.append('page', pagination.value.page.toString())
    params.append('pageSize', pagination.value.pageSize.toString())
    if (filters.value.searchValue) {
      params.append('searchType', filters.value.searchType)
      params.append('searchValue', filters.value.searchValue)
    }
    const res = await api.get<{ total: number; data: Node[] }>(`/vms/api/mtNode/list?${params}`)
    nodes.value = res.data
    pagination.value.total = res.total
  } catch (e: any) {
    ElMessage.error('加载点位失败')
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.value = { searchType: 'nodeName', searchValue: '' }
  pagination.value.page = 1
  loadNodes()
}

async function loadStaffOptions() {
  try {
    staffOptions.value = await api.get<Staff[]>('/vms/api/taskOptions/deliveryStaff')
  } catch (e) {
    // ignore
  }
}

function showDialog(row?: Node) {
  if (row) {
    form.value = {
      id: row.id,
      nodeName: row.nodeName,
      nodeAddress: row.nodeAddress,
      deliveryId: row.deliveryId,
      repairId: row.repairId,
      propertyName: row.propertyName || '',
      propertyManager: row.propertyManager || '',
      propertyPhone: row.propertyPhone || '',
    }
  } else {
    form.value = {
      id: undefined,
      nodeName: '',
      nodeAddress: '',
      deliveryId: undefined,
      repairId: undefined,
      propertyName: '',
      propertyManager: '',
      propertyPhone: '',
    }
  }
  dialogVisible.value = true
  if (staffOptions.value.length === 0) loadStaffOptions()
}

async function submitForm() {
  if (!form.value.nodeName) {
    ElMessage.warning('请输入点位名称')
    return
  }
  submitting.value = true
  try {
    const formData = new FormData()
    formData.append('nodeName', form.value.nodeName)
    if (form.value.nodeAddress) formData.append('nodeAddress', form.value.nodeAddress)
    if (form.value.deliveryId !== undefined) formData.append('deliveryId', form.value.deliveryId.toString())
    if (form.value.repairId !== undefined) formData.append('repairId', form.value.repairId.toString())
    if (form.value.propertyName) formData.append('propertyName', form.value.propertyName)
    if (form.value.propertyManager) formData.append('propertyManager', form.value.propertyManager)
    if (form.value.propertyPhone) formData.append('propertyPhone', form.value.propertyPhone)

    const url = form.value.id
      ? `/vms/api/mtNode/${form.value.id}/update`
      : '/vms/api/mtNode/create'
    const res = await api.post<any>(url, formData)
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '保存成功')
      dialogVisible.value = false
      loadNodes()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

async function deleteNode(row: Node) {
  try {
    await ElMessageBox.confirm(`确定删除点位「${row.nodeName}」吗？`, '确认删除', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>(`/vms/api/mtNode/${row.id}/delete`, new FormData())
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '删除成功')
      loadNodes()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e: any) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => loadNodes())
</script>

<style scoped>
.node-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.action-bar { margin-bottom: 16px; }
.filter-form { margin-bottom: 16px; padding: 16px; background: white; border-radius: 4px; }
</style>
