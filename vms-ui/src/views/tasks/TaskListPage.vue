<template>
  <div class="task-page">
    <h1>{{ config.title }}</h1>
    <p class="hint">{{ config.hint }}</p>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="showCreateDialog">新建工单</el-button>
    </div>

    <!-- 筛选栏 -->
    <el-form :inline="true" :model="filters" class="filter-form">
      <el-form-item label="状态">
        <el-select v-model="filters.status" placeholder="全部" clearable>
          <el-option label="未开始" :value="1" />
          <el-option label="进行中" :value="2" />
          <el-option label="完成" :value="4" />
          <el-option label="关闭" :value="7" />
        </el-select>
      </el-form-item>
      <el-form-item label="搜索">
        <el-select v-model="filters.searchField" placeholder="搜索字段" style="width: 120px">
          <el-option label="售货机编号" :value="1" />
          <el-option label="点位名称" :value="2" />
          <el-option label="负责人" :value="3" />
        </el-select>
        <el-input v-model="filters.searchValue" placeholder="搜索值" style="width: 200px; margin-left: 10px" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadTasks">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工单列表 -->
    <el-table :data="tasks" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="工单标题" min-width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="vmInnerCode" label="售货机编号" width="150" />
      <el-table-column v-if="config.showRemarks" prop="remarks" label="备注" min-width="150" />
      <el-table-column prop="ownerId" label="负责人ID" width="100" />
      <el-table-column prop="ctime" label="创建时间" width="180">
        <template #default="scope">{{ formatDate(scope.row.ctime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="showDetail(scope.row)">详情</el-button>
          <el-button
            v-if="scope.row.status !== 4 && scope.row.status !== 7"
            type="warning"
            link
            @click="showEditDialog(scope.row)"
          >
            修改
          </el-button>
          <el-button
            v-if="config.showPublish && scope.row.status !== 7"
            type="success"
            link
            @click="publishTask(scope.row)"
          >
            发布
          </el-button>
          <el-button
            v-if="scope.row.status !== 4 && scope.row.status !== 7"
            type="danger"
            link
            @click="cancelTask(scope.row)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadTasks"
      @current-change="loadTasks"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentTask">
        <el-descriptions-item label="工单ID">{{ currentTask.id }}</el-descriptions-item>
        <el-descriptions-item label="工单标题">{{ currentTask.title }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentTask.status)">{{ getStatusText(currentTask.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="售货机编号">{{ currentTask.vmInnerCode }}</el-descriptions-item>
        <el-descriptions-item label="负责人ID">{{ currentTask.ownerId }}</el-descriptions-item>
        <el-descriptions-item v-if="config.showRemarks" label="备注">{{ currentTask.remarks || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentTask.ctime) }}</el-descriptions-item>
        <el-descriptions-item label="执行时间">{{ formatDate(currentTask.taskTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="currentTask.finishTime" label="完成时间">{{ formatDate(currentTask.finishTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="currentTask.company" label="所属公司">
          {{ currentTask.company.name }} ({{ currentTask.company.level }}级)
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新建工单对话框 -->
    <el-dialog v-model="createVisible" title="新建工单" width="600px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="售货机" prop="vmId">
          <el-select
            v-model="createForm.vmId"
            filterable
            remote
            reserve-keyword
            placeholder="请输入售货机编号搜索"
            :remote-method="searchVendingMachines"
            :loading="vmLoading"
          >
            <el-option
              v-for="vm in vmOptions"
              :key="vm.id"
              :label="`${vm.innerCode} - ${vm.vmDesc || '未命名'}`"
              :value="vm.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="ownerId">
          <el-select v-model="createForm.ownerId" filterable placeholder="请选择负责人">
            <el-option
              v-for="staff in staffOptions"
              :key="staff.id"
              :label="`${staff.userName} (${staff.userAccount})`"
              :value="staff.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="config.showRemarks" label="备注">
          <el-input v-model="createForm.remarks" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="执行时间">
          <el-date-picker
            v-model="createForm.taskTime"
            type="datetime"
            placeholder="选择执行时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreateForm" :loading="submitting">创建</el-button>
      </template>
    </el-dialog>

    <!-- 编辑工单对话框 -->
    <el-dialog v-model="editVisible" title="修改工单" width="600px">
      <el-form :model="editForm" ref="editFormRef" label-width="100px">
        <el-form-item label="工单ID">
          <el-input :value="editForm.taskId" disabled />
        </el-form-item>
        <el-form-item label="负责人">
          <el-select v-model="editForm.editOwnerId" filterable placeholder="请选择负责人" clearable>
            <el-option
              v-for="staff in staffOptions"
              :key="staff.id"
              :label="`${staff.userName} (${staff.userAccount})`"
              :value="staff.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="config.showRemarks" label="备注">
          <el-input v-model="editForm.remarks" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="执行时间">
          <el-date-picker
            v-model="editForm.taskTime"
            type="datetime"
            placeholder="选择执行时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditForm" :loading="editing">保存</el-button>
      </template>
    </el-dialog>

    <!-- 取消确认对话框 -->
    <el-dialog v-model="cancelVisible" title="确认取消" width="400px">
      <p>确定要取消工单 <strong>{{ cancelTaskId }}</strong> 吗？</p>
      <template #footer>
        <el-button @click="cancelVisible = false">返回</el-button>
        <el-button type="danger" @click="confirmCancel" :loading="cancelling">确认取消</el-button>
      </template>
    </el-dialog>

    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { api } from '../../api/client'

interface Task {
  id: number
  title: string
  type: number
  status: number
  vmId: number
  vmInnerCode: string
  ownerId: number
  remarks?: string
  ctime: string
  taskTime: string
  finishTime?: string
  company?: { id: number; name: string; level: number }
}

interface VendingMachine {
  id: number
  innerCode: string
  vmDesc: string
  nodeId: number
  nodeName: string
  status: number
}

interface Staff {
  id: number
  userName: string
  userAccount: string
  companyId: number
  companyName: string
}

interface PageConfig {
  title: string
  hint: string
  apiPath: string
  showRemarks: boolean
  showPublish: boolean
}

const props = defineProps<{ config: PageConfig }>()

const tasks = ref<Task[]>([])
const loading = ref(false)
const error = ref('')

const filters = ref({
  status: undefined as number | undefined,
  searchField: undefined as number | undefined,
  searchValue: '',
})

const pagination = ref({ page: 1, pageSize: 10, total: 0 })

const detailVisible = ref(false)
const currentTask = ref<Task | null>(null)

const createVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = ref({
  vmId: undefined as number | undefined,
  ownerId: undefined as number | undefined,
  remarks: '',
  taskTime: '',
})
const createRules = {
  vmId: [{ required: true, message: '请选择售货机', trigger: 'change' }],
  ownerId: [{ required: true, message: '请选择负责人', trigger: 'change' }],
}
const vmOptions = ref<VendingMachine[]>([])
const staffOptions = ref<Staff[]>([])
const vmLoading = ref(false)
const submitting = ref(false)

const editVisible = ref(false)
const editFormRef = ref<FormInstance>()
const editForm = ref({
  taskId: 0,
  editOwnerId: undefined as number | undefined,
  remarks: '',
  taskTime: '',
})
const editing = ref(false)

const cancelVisible = ref(false)
const cancelTaskId = ref<number | null>(null)
const cancelling = ref(false)

function getStatusType(status: number) {
  const types: Record<number, string> = { 1: 'info', 2: 'warning', 4: 'success', 7: 'danger' }
  return types[status] || 'info'
}

function getStatusText(status: number) {
  const texts: Record<number, string> = { 1: '未开始', 2: '进行中', 4: '完成', 7: '关闭' }
  return texts[status] || '未知'
}

function formatDate(dateStr: string | null) {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

async function loadTasks() {
  loading.value = true
  error.value = ''
  try {
    const params = new URLSearchParams()
    params.append('page', pagination.value.page.toString())
    params.append('pageSize', pagination.value.pageSize.toString())
    if (filters.value.status !== undefined) params.append('status', filters.value.status.toString())
    if (filters.value.searchField !== undefined) params.append('searchField', filters.value.searchField.toString())
    if (filters.value.searchValue) params.append('searchValue', filters.value.searchValue)

    const response = await api.get<{ total: number; page: number; pageSize: number; data: Task[] }>(
      `${props.config.apiPath}/list?${params.toString()}`,
    )
    tasks.value = response.data
    pagination.value.total = response.total
  } catch (e: any) {
    error.value = e.message || '加载失败'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.value = { status: undefined, searchField: undefined, searchValue: '' }
  pagination.value.page = 1
  loadTasks()
}

function showDetail(task: Task) {
  currentTask.value = task
  detailVisible.value = true
}

function showCreateDialog() {
  createForm.value = { vmId: undefined, ownerId: undefined, remarks: '', taskTime: '' }
  createVisible.value = true
  if (staffOptions.value.length === 0) loadStaffOptions()
}

async function searchVendingMachines(query: string) {
  if (!query) {
    vmOptions.value = []
    return
  }
  vmLoading.value = true
  try {
    const response = await api.get<VendingMachine[]>(
      `/vms/api/taskOptions/vendingMachines?searchInnerCode=${encodeURIComponent(query)}&pageSize=20`,
    )
    vmOptions.value = response
  } catch (e) {
    ElMessage.error('搜索售货机失败')
  } finally {
    vmLoading.value = false
  }
}

async function loadStaffOptions() {
  try {
    const response = await api.get<Staff[]>('/vms/api/taskOptions/deliveryStaff')
    staffOptions.value = response
  } catch (e) {
    ElMessage.error('加载配送员列表失败')
  }
}

async function submitCreateForm() {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const formData = new FormData()
      formData.append('vmId', createForm.value.vmId!.toString())
      formData.append('ownerId', createForm.value.ownerId!.toString())
      if (props.config.showRemarks && createForm.value.remarks) formData.append('remarks', createForm.value.remarks)
      if (createForm.value.taskTime) formData.append('taskTime', createForm.value.taskTime)

      const response = await api.post<{ success: boolean; message: string }>(
        `${props.config.apiPath}/create`,
        formData,
      )
      if (response.success) {
        ElMessage.success(response.message)
        createVisible.value = false
        loadTasks()
      } else {
        ElMessage.error(response.message)
      }
    } catch (e: any) {
      ElMessage.error('创建工单失败')
    } finally {
      submitting.value = false
    }
  })
}

function showEditDialog(task: Task) {
  editForm.value = {
    taskId: task.id,
    editOwnerId: task.ownerId || undefined,
    remarks: task.remarks || '',
    taskTime: '',
  }
  editVisible.value = true
  if (staffOptions.value.length === 0) loadStaffOptions()
}

async function submitEditForm() {
  editing.value = true
  try {
    const formData = new FormData()
    if (editForm.value.editOwnerId !== undefined) formData.append('editOwnerId', editForm.value.editOwnerId.toString())
    if (props.config.showRemarks && editForm.value.remarks) formData.append('remarks', editForm.value.remarks)
    if (editForm.value.taskTime) formData.append('taskTime', editForm.value.taskTime)

    const response = await api.post<{ success: boolean; message: string }>(
      `${props.config.apiPath}/${editForm.value.taskId}/edit`,
      formData,
    )
    if (response.success) {
      ElMessage.success(response.message)
      editVisible.value = false
      loadTasks()
    } else {
      ElMessage.error(response.message)
    }
  } catch (e: any) {
    ElMessage.error('修改工单失败')
  } finally {
    editing.value = false
  }
}

async function publishTask(task: Task) {
  try {
    await ElMessageBox.confirm(`确定要发布工单 ${task.id} 吗？`, '确认发布', { type: 'warning' })
  } catch {
    return
  }
  try {
    const response = await api.post<{ success: boolean; message: string }>(
      `${props.config.apiPath}/${task.id}/publish`,
      new FormData(),
    )
    if (response.success) {
      ElMessage.success(response.message)
      loadTasks()
    } else {
      ElMessage.error(response.message)
    }
  } catch (e: any) {
    ElMessage.error('发布工单失败')
  }
}

function cancelTask(task: Task) {
  cancelTaskId.value = task.id
  cancelVisible.value = true
}

async function confirmCancel() {
  if (cancelTaskId.value === null) return
  cancelling.value = true
  try {
    const response = await api.post<{ success: boolean; message: string }>(
      `${props.config.apiPath}/${cancelTaskId.value}/cancel`,
      new FormData(),
    )
    if (response.success) {
      ElMessage.success(response.message)
      cancelVisible.value = false
      loadTasks()
    } else {
      ElMessage.error(response.message)
    }
  } catch (e: any) {
    ElMessage.error('取消工单失败')
  } finally {
    cancelling.value = false
  }
}

onMounted(() => loadTasks())
</script>

<style scoped>
.task-page h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.action-bar { margin-bottom: 20px; }
.filter-form { margin-bottom: 20px; padding: 20px; background: white; border-radius: 4px; }
.error { margin-top: 16px; padding: 10px; background: #fee; color: #c0392b; border-radius: 4px; font-size: 13px; }
</style>
