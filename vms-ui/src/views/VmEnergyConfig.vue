<template>
  <div class="energy-config">
    <h1>设备节能配置</h1>
    <p class="hint">配置设备节能时段，并下发到设备。</p>

    <!-- 选择设备 -->
    <el-form :inline="true" class="filter-form">
      <el-form-item label="设备">
        <el-select
          v-model="selectedVmId"
          filterable
          remote
          :remote-method="searchVms"
          :loading="vmLoading"
          placeholder="搜索设备编号"
          style="width: 260px"
          @change="loadConfigs"
        >
          <el-option
            v-for="vm in vmOptions"
            :key="vm.id"
            :label="`${vm.innerCode} - ${vm.vmDesc || '未命名'}`"
            :value="vm.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="showDialog()" :disabled="!selectedVmId">新增配置</el-button>
        <el-button type="success" @click="pushToMcs" :disabled="!selectedVmId" :loading="pushing">
          下发到设备
        </el-button>
      </el-form-item>
    </el-form>

    <div v-if="!selectedVmId" class="empty-tip">请先选择设备</div>
    <template v-else>
      <el-table :data="configs" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="energySaveType" label="节能类型" width="100" />
        <el-table-column prop="targetValue" label="目标值" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="endTime" label="结束时间" width="120" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="showDialog(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="deleteConfig(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑节能配置' : '新增节能配置'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="节能类型">
          <el-input-number v-model="form.energySaveType" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="目标值">
          <el-input-number v-model="form.targetValue" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-input v-model="form.startTime" placeholder="如：22:00" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-input v-model="form.endTime" placeholder="如：06:00" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface EnergyConfig {
  id: number
  energySaveType: number
  targetValue: number
  startTime: string
  endTime: string
  remark: string
  tempBoxNo: number
}

interface VmOption {
  id: number
  innerCode: string
  vmDesc: string
}

const route = useRoute()
const selectedVmId = ref<number | undefined>(undefined)
const vmOptions = ref<VmOption[]>([])
const vmLoading = ref(false)

const configs = ref<EnergyConfig[]>([])
const loading = ref(false)
const pushing = ref(false)

const dialogVisible = ref(false)
const form = ref({
  id: undefined as number | undefined,
  energySaveType: 0,
  targetValue: 0,
  startTime: '',
  endTime: '',
  remark: '',
})
const submitting = ref(false)

async function searchVms(query: string) {
  if (!query) {
    vmOptions.value = []
    return
  }
  vmLoading.value = true
  try {
    const res = await api.get<{ total: number; data: VmOption[] }>(
      `/vms/api/mtVm/list?page=1&pageSize=20&filedType=1&filedValue=${encodeURIComponent(query)}`,
    )
    vmOptions.value = res.data
  } catch (e) {
    ElMessage.error('搜索设备失败')
  } finally {
    vmLoading.value = false
  }
}

async function loadConfigs() {
  if (!selectedVmId.value) return
  loading.value = true
  try {
    configs.value = await api.get<EnergyConfig[]>(`/vms/api/vmEnergy/${selectedVmId.value}/list`)
  } catch (e: any) {
    ElMessage.error('加载节能配置失败')
  } finally {
    loading.value = false
  }
}

function showDialog(row?: EnergyConfig) {
  if (row) {
    form.value = {
      id: row.id,
      energySaveType: row.energySaveType || 0,
      targetValue: row.targetValue || 0,
      startTime: row.startTime || '',
      endTime: row.endTime || '',
      remark: row.remark || '',
    }
  } else {
    form.value = { id: undefined, energySaveType: 0, targetValue: 0, startTime: '', endTime: '', remark: '' }
  }
  dialogVisible.value = true
}

async function submitForm() {
  if (!selectedVmId.value) return
  submitting.value = true
  try {
    const formData = new FormData()
    formData.append('vmId', selectedVmId.value.toString())
    formData.append('energySaveType', form.value.energySaveType.toString())
    formData.append('targetValue', form.value.targetValue.toString())
    if (form.value.startTime) formData.append('startTime', form.value.startTime)
    if (form.value.endTime) formData.append('endTime', form.value.endTime)
    if (form.value.remark) formData.append('remark', form.value.remark)
    if (form.value.id) formData.append('energySaveId', form.value.id.toString())

    const res = await api.post<any>(`/vms/api/vmEnergy/save`, formData)
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '保存成功')
      dialogVisible.value = false
      loadConfigs()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

async function deleteConfig(row: EnergyConfig) {
  try {
    await ElMessageBox.confirm(`确定删除节能配置 ${row.id} 吗？`, '确认删除', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>(`/vms/api/vmEnergy/${row.id}/delete`, new FormData())
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '删除成功')
      loadConfigs()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e: any) {
    ElMessage.error('删除失败')
  }
}

async function pushToMcs() {
  if (!selectedVmId.value) return
  pushing.value = true
  try {
    const res = await api.post<any>(`/vms/api/vmEnergy/${selectedVmId.value}/push`, new FormData())
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '下发成功')
    } else {
      ElMessage.error(res.message || '下发失败')
    }
  } catch (e: any) {
    ElMessage.error('下发失败')
  } finally {
    pushing.value = false
  }
}

onMounted(() => {
  const vmIdParam = route.query.vmId
  if (vmIdParam) {
    selectedVmId.value = Number(vmIdParam)
    loadConfigs()
  }
})
</script>

<style scoped>
.energy-config h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { margin-bottom: 16px; padding: 16px; background: white; border-radius: 4px; }
.empty-tip { color: #95a5a6; text-align: center; padding: 40px 0; }
</style>
