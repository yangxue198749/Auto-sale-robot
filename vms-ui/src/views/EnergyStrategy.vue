<template>
  <div class="energy-strategy">
    <h1>节能策略</h1>
    <p class="hint">按售货机型号配置制冷/制热/关闭屏幕的节能时段与目标温度。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="策略名称">
        <el-input v-model="filterName" placeholder="策略名称" clearable style="width: 180px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item label="型号">
        <el-select v-model="filterModelId" filterable clearable placeholder="全部型号" style="width: 200px">
          <el-option v-for="m in models" :key="m.id" :label="m.modelName" :value="m.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openCreateStrategy">新建策略</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="deleteSelected">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="strategyName" label="策略名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="strategyDesc" label="描述" min-width="160" show-overflow-tooltip />
      <el-table-column prop="vmModelName" label="售货机型号" min-width="140" />
      <el-table-column prop="detailCount" label="节能项" width="80" />
      <el-table-column label="创建" min-width="150">
        <template #default="scope">
          <div>{{ scope.row.ctorName || '-' }}</div>
          <div class="time">{{ formatTime(scope.row.ctime) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="更新" min-width="150">
        <template #default="scope">
          <div>{{ scope.row.utorName || '-' }}</div>
          <div class="time">{{ formatTime(scope.row.utime) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row)">查看</el-button>
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

    <!-- 新建策略 -->
    <el-dialog v-model="createVisible" title="新建节能策略" width="480px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="售货机型号" required>
          <el-select v-model="createForm.vmModelId" filterable placeholder="选择型号" style="width: 100%">
            <el-option v-for="m in models" :key="m.id" :label="m.modelName" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="策略名称" required>
          <el-input v-model="createForm.strategyName" maxlength="60" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.strategyDesc" type="textarea" :rows="2" maxlength="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="createStrategy">创建</el-button>
      </template>
    </el-dialog>

    <!-- 策略详情 -->
    <el-dialog v-model="detailVisible" :title="`节能策略：${detail?.strategyName || ''}`" width="760px">
      <div v-loading="detailLoading">
        <el-alert
          v-if="detail?.tempCtrlDesc"
          :title="detail.tempCtrlDesc"
          type="warning"
          :closable="false"
          show-icon
          style="margin-bottom: 12px"
        />
        <el-table :data="detail?.details || []" size="small" border style="width: 100%">
          <el-table-column label="类型" width="100">
            <template #default="scope">{{ energyTypeText(scope.row.energySaveType) }}</template>
          </el-table-column>
          <el-table-column prop="tempBoxNo" label="保温箱" width="80" />
          <el-table-column label="目标温度" width="90">
            <template #default="scope">{{ scope.row.targetValue ?? '-' }}</template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始" width="80" />
          <el-table-column prop="endTime" label="结束" width="80" />
          <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="openDetailEditor(scope.row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="deleteDetail(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="actions">
          <el-button type="primary" :disabled="!detail" @click="openDetailEditor()">新增节能项</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 节能项编辑 -->
    <el-dialog v-model="itemVisible" :title="itemForm.energySaveId ? '编辑节能项' : '新增节能项'" width="480px">
      <el-form :model="itemForm" label-width="90px">
        <el-form-item label="节能类型" required>
          <el-radio-group v-model="itemForm.energySaveType">
            <el-radio v-for="opt in energyTypeOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="itemForm.energySaveType !== 3" label="保温箱">
          <el-select v-model="itemForm.tempBoxNo" placeholder="选择保温箱" style="width: 100%">
            <el-option v-for="n in tempBoxOptions" :key="n" :label="`保温箱 ${n}`" :value="n" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="itemForm.energySaveType !== 3" label="目标温度">
          <el-input-number v-model="itemForm.targetValue" :min="-30" :max="60" controls-position="right" style="width: 140px" />
          <span class="unit">℃</span>
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-time-select v-model="itemForm.startTime" start="00:00" step="00:30" end="23:30" placeholder="开始时间" style="width: 130px" />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-time-select v-model="itemForm.endTime" start="00:00" step="00:30" end="23:30" placeholder="结束时间" style="width: 130px" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="itemForm.remark" maxlength="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingItem" @click="saveItem">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface ModelOption {
  id: number
  modelName: string
}

interface StrategyRow {
  strategyId: number
  strategyName: string
  strategyDesc: string
  vmModelId: number | null
  vmModelName: string | null
  detailCount: number
  ctorName: string | null
  ctime: string | null
  utorName: string | null
  utime: string | null
}

interface EnergyDetail {
  energySaveId: number
  energySaveType: number
  targetValue: number | null
  tempBoxNo: number | null
  startTime: string | null
  endTime: string | null
  remark: string | null
}

interface StrategyDetail {
  strategyId: number
  strategyName: string
  strategyDesc: string
  vmModelId?: number
  vmModelName?: string
  temperatureHandlerMode?: number
  screenHave?: number
  tempBoxCount?: number
  tempCtrlDesc?: string
  details: EnergyDetail[]
}

const models = ref<ModelOption[]>([])
const rows = ref<StrategyRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterName = ref('')
const filterModelId = ref<number | undefined>(undefined)
const selectedRows = ref<StrategyRow[]>([])

const createVisible = ref(false)
const creating = ref(false)
const createForm = ref({ vmModelId: undefined as number | undefined, strategyName: '', strategyDesc: '' })

const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref<StrategyDetail | null>(null)

const itemVisible = ref(false)
const savingItem = ref(false)
const itemForm = ref<{
  energySaveId?: number
  energySaveType: number
  targetValue: number | undefined
  tempBoxNo: number | undefined
  startTime: string
  endTime: string
  remark: string
}>({ energySaveType: 1, targetValue: undefined, tempBoxNo: undefined, startTime: '', endTime: '', remark: '' })

const energyTypeOptions = computed(() => {
  const mode = detail.value?.temperatureHandlerMode
  const options: { value: number; label: string }[] = []
  if (mode === 29 || mode === 31) options.push({ value: 1, label: '制冷' })
  if (mode === 30 || mode === 31) options.push({ value: 2, label: '制热' })
  if (detail.value?.screenHave === 1) options.push({ value: 3, label: '关闭屏幕' })
  if (!options.length) options.push({ value: 1, label: '制冷' })
  return options
})

const tempBoxOptions = computed(() => {
  const count = detail.value?.tempBoxCount || 1
  const arr: number[] = []
  for (let i = 1; i <= count; i++) arr.push(i)
  return arr
})

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

function energyTypeText(type: number) {
  if (type === 1) return '制冷'
  if (type === 2) return '制热'
  if (type === 3) return '关闭屏幕'
  return '-'
}

async function loadModels() {
  try {
    const res = await api.get<{ data: ModelOption[] }>('/vms/api/vmModel/list?page=1&pageSize=100')
    models.value = res.data
  } catch (e) {
    ElMessage.error('加载型号失败')
  }
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterName.value) params.set('searchStVmName', filterName.value)
    if (filterModelId.value) params.set('searchVmModelId', String(filterModelId.value))
    const res = await api.get<{ total: number; data: StrategyRow[] }>(`/vms/api/stVmEnergySave/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载策略列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: StrategyRow[]) {
  selectedRows.value = selection
}

function openCreateStrategy() {
  createForm.value = { vmModelId: undefined, strategyName: '', strategyDesc: '' }
  createVisible.value = true
}

async function createStrategy() {
  if (!createForm.value.vmModelId || !createForm.value.strategyName) {
    ElMessage.warning('请选择型号并填写策略名称')
    return
  }
  creating.value = true
  try {
    const res = await api.post<any>('/vms/api/stVmEnergySave/create', {
      vmModelId: createForm.value.vmModelId,
      strategyName: createForm.value.strategyName,
      strategyDesc: createForm.value.strategyDesc,
    }, { form: true })
    handleResult(res, '创建成功')
    createVisible.value = false
    loadList()
  } catch (e) {
    ElMessage.error('创建失败')
  } finally {
    creating.value = false
  }
}

async function deleteSelected() {
  if (!selectedRows.value.length) return
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个策略？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/stVmEnergySave/delete', {
      ids: selectedRows.value.map((r) => r.strategyId).join(','),
    }, { form: true })
    handleResult(res, '删除成功')
    loadList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

async function openDetail(row: StrategyRow) {
  detailVisible.value = true
  await loadDetail(row.strategyId)
}

async function loadDetail(strategyId: number) {
  detailLoading.value = true
  try {
    detail.value = await api.get<StrategyDetail>(`/vms/api/stVmEnergySave/${strategyId}`)
  } catch (e) {
    ElMessage.error('加载策略详情失败')
  } finally {
    detailLoading.value = false
  }
}

function openDetailEditor(row?: EnergyDetail) {
  itemForm.value = row
    ? {
        energySaveId: row.energySaveId,
        energySaveType: row.energySaveType,
        targetValue: row.targetValue ?? undefined,
        tempBoxNo: row.tempBoxNo ?? undefined,
        startTime: row.startTime || '',
        endTime: row.endTime || '',
        remark: row.remark || '',
      }
    : {
        energySaveType: energyTypeOptions.value[0]?.value ?? 1,
        targetValue: undefined,
        tempBoxNo: tempBoxOptions.value[0],
        startTime: '',
        endTime: '',
        remark: '',
      }
  itemVisible.value = true
}

async function saveItem() {
  if (!detail.value) return
  if (!itemForm.value.startTime || !itemForm.value.endTime) {
    ElMessage.warning('请填写开始和结束时间')
    return
  }
  savingItem.value = true
  try {
    const payload: Record<string, any> = {
      energySaveType: itemForm.value.energySaveType,
      startTime: itemForm.value.startTime,
      endTime: itemForm.value.endTime,
      remark: itemForm.value.remark,
    }
    if (itemForm.value.energySaveType !== 3) {
      payload.targetValue = itemForm.value.targetValue
      payload.tempBoxNo = itemForm.value.tempBoxNo
    }
    let path = '/vms/api/stVmEnergySave/detail/create'
    if (itemForm.value.energySaveId) {
      path = '/vms/api/stVmEnergySave/detail/edit'
      payload.energySaveId = itemForm.value.energySaveId
    } else {
      payload.strategyId = detail.value.strategyId
    }
    const res = await api.post<any>(path, payload, { form: true })
    handleResult(res, '保存成功')
    itemVisible.value = false
    await loadDetail(detail.value.strategyId)
    loadList()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    savingItem.value = false
  }
}

async function deleteDetail(row: EnergyDetail) {
  if (!detail.value) return
  try {
    const res = await api.post<any>('/vms/api/stVmEnergySave/detail/delete', { ids: String(row.energySaveId) }, { form: true })
    handleResult(res, '删除成功')
    await loadDetail(detail.value.strategyId)
    loadList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function handleResult(res: any, successMessage: string) {
  if (res?.success || res?.type === 'success') {
    ElMessage.success(res?.message || successMessage)
  } else {
    ElMessage.error(res?.message || '操作失败')
  }
}

onMounted(() => {
  loadModels()
  loadList()
})
</script>

<style scoped>
.energy-strategy h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.time { color: #95a5a6; font-size: 12px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.actions { margin-top: 12px; }
.unit { margin-left: 6px; color: #909399; }
</style>
