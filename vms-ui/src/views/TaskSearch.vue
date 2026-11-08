<template>
  <div class="task-search">
    <h1>工单查询</h1>
    <p class="hint">四类工单统一查询（配送/维护/投放/撤机），支持导出 Excel。</p>

    <!-- 筛选栏 -->
    <el-form :inline="true" :model="filters" class="filter-form">
      <el-form-item label="工单类型">
        <el-select v-model="filters.taskType" placeholder="全部类型" clearable style="width: 140px">
          <el-option v-for="t in taskTypes" :key="t.value" :label="t.label" :value="t.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.status" placeholder="全部" clearable style="width: 120px">
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
        <el-input v-model="filters.searchValue" placeholder="搜索值" style="width: 180px; margin-left: 10px" clearable />
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker
          v-model="filters.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          style="width: 260px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadTasks">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
        <el-button type="success" @click="exportSelected" :disabled="selectedIds.length === 0">
          导出选中 ({{ selectedIds.length }})
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 工单列表 -->
    <el-table :data="tasks" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="工单标题" min-width="200" />
      <el-table-column prop="typeName" label="类型" width="90">
        <template #default="scope">
          <el-tag :type="getTypeTagType(scope.row.type)" size="small">{{ scope.row.typeName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="vmInnerCode" label="售货机编号" width="140" />
      <el-table-column prop="ownerId" label="负责人" width="90" />
      <el-table-column prop="remarks" label="备注" min-width="150" show-overflow-tooltip />
      <el-table-column prop="ctime" label="创建时间" width="170">
        <template #default="scope">{{ formatDate(scope.row.ctime) }}</template>
      </el-table-column>
      <el-table-column prop="taskTime" label="执行时间" width="170">
        <template #default="scope">{{ formatDate(scope.row.taskTime) }}</template>
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

    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface Task {
  id: number
  title: string
  type: number
  typeName: string
  status: number
  vmInnerCode: string
  ownerId: number
  remarks?: string
  ctime: string
  taskTime: string
}

interface TypeOption {
  value: number
  label: string
}

const tasks = ref<Task[]>([])
const loading = ref(false)
const error = ref('')
const taskTypes = ref<TypeOption[]>([])
const selectedIds = ref<number[]>([])

const filters = ref({
  taskType: undefined as number | undefined,
  status: undefined as number | undefined,
  searchField: undefined as number | undefined,
  searchValue: '',
  dateRange: [] as string[],
})

const pagination = ref({ page: 1, pageSize: 10, total: 0 })

function getStatusType(status: number) {
  const types: Record<number, string> = { 1: 'info', 2: 'warning', 4: 'success', 7: 'danger' }
  return types[status] || 'info'
}

function getStatusText(status: number) {
  const texts: Record<number, string> = { 1: '未开始', 2: '进行中', 4: '完成', 7: '关闭' }
  return texts[status] || '未知'
}

function getTypeTagType(type: number) {
  const types: Record<number, string> = { 3: '', 5: 'warning', 7: 'success', 8: 'info' }
  return types[type] || 'info'
}

function formatDate(dateStr: string | null) {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
}

function onSelectionChange(rows: Task[]) {
  selectedIds.value = rows.map((r) => r.id)
}

async function loadTaskTypes() {
  try {
    taskTypes.value = await api.get<TypeOption[]>('/vms/api/taskSearch/taskTypes')
  } catch (e) {
    // ignore, use defaults
  }
}

async function loadTasks() {
  loading.value = true
  error.value = ''
  try {
    const params = new URLSearchParams()
    params.append('page', pagination.value.page.toString())
    params.append('pageSize', pagination.value.pageSize.toString())
    if (filters.value.taskType !== undefined) params.append('taskType', filters.value.taskType.toString())
    if (filters.value.status !== undefined) params.append('status', filters.value.status.toString())
    if (filters.value.searchField !== undefined) params.append('searchField', filters.value.searchField.toString())
    if (filters.value.searchValue) params.append('searchValue', filters.value.searchValue)
    if (filters.value.dateRange && filters.value.dateRange.length === 2) {
      params.append('startTime', filters.value.dateRange[0])
      params.append('endTime', filters.value.dateRange[1])
    }

    const response = await api.get<{ total: number; page: number; pageSize: number; data: Task[] }>(
      `/vms/api/taskSearch/list?${params.toString()}`,
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
  filters.value = {
    taskType: undefined,
    status: undefined,
    searchField: undefined,
    searchValue: '',
    dateRange: [],
  }
  pagination.value.page = 1
  loadTasks()
}

function exportSelected() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要导出的工单')
    return
  }
  // 复用现有 JSP 导出端点（Shiro authc 保护，浏览器直接下载）
  const params = selectedIds.value.map((id) => `taskVmIds=${id}`).join('&')
  window.open(`/api/searchTask/exportWorkOrder?${params}`, '_blank')
}

onMounted(() => {
  loadTaskTypes()
  loadTasks()
})
</script>

<style scoped>
.task-search h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { margin-bottom: 20px; padding: 20px; background: white; border-radius: 4px; }
.error { margin-top: 16px; padding: 10px; background: #fee; color: #c0392b; border-radius: 4px; font-size: 13px; }
</style>
