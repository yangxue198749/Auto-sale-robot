<template>
  <div class="price-config">
    <h1>设备价格配置</h1>
    <p class="hint">查看价格策略明细，并将策略应用到售货机设备。</p>

    <div class="layout">
      <!-- 左侧：价格策略 -->
      <div class="panel">
        <div class="panel-header">价格策略</div>
        <el-input v-model="strategyName" placeholder="搜索策略名称" size="small" clearable style="margin-bottom: 10px" @input="loadStrategies" />
        <el-table
          :data="strategies"
          v-loading="strategiesLoading"
          highlight-current-row
          size="small"
          @current-change="onStrategySelect"
          style="width: 100%"
        >
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="strategyName" label="策略名称" min-width="140" />
        </el-table>
      </div>

      <!-- 右侧：策略明细 + 应用 -->
      <div class="panel right-panel">
        <div class="panel-header">
          策略明细
          <template v-if="selectedStrategy"> — {{ selectedStrategy.strategyName }}</template>
        </div>
        <div v-if="!selectedStrategy" class="empty-tip">请先选择左侧价格策略</div>
        <template v-else>
          <el-table :data="prices" v-loading="pricesLoading" size="small" border style="width: 100%; margin-bottom: 16px">
            <el-table-column prop="skuName" label="商品" min-width="150" />
            <el-table-column prop="skuPrice" label="价格(分)" width="110" />
          </el-table>
          <el-form label-width="90px">
            <el-form-item label="目标设备">
              <el-select
                v-model="selectedVmIds"
                multiple
                filterable
                remote
                :remote-method="searchVms"
                :loading="vmLoading"
                placeholder="搜索设备编号"
                style="width: 100%"
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
              <el-button type="primary" :loading="applying" :disabled="selectedVmIds.length === 0" @click="applyPrice">
                应用策略到选中设备 ({{ selectedVmIds.length }})
              </el-button>
            </el-form-item>
          </el-form>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface Strategy {
  id: number
  strategyName: string
  strategyType: number
}

interface Price {
  id: number
  skuId: number
  skuName: string
  skuPrice: number
}

interface VmOption {
  id: number
  innerCode: string
  vmDesc: string
}

const strategies = ref<Strategy[]>([])
const strategiesLoading = ref(false)
const strategyName = ref('')

const selectedStrategy = ref<Strategy | null>(null)
const prices = ref<Price[]>([])
const pricesLoading = ref(false)

const selectedVmIds = ref<number[]>([])
const vmOptions = ref<VmOption[]>([])
const vmLoading = ref(false)
const applying = ref(false)

async function loadStrategies() {
  strategiesLoading.value = true
  try {
    const params = new URLSearchParams()
    if (strategyName.value) params.append('strategyName', strategyName.value)
    strategies.value = await api.get<Strategy[]>(`/vms/api/vmPrice/strategies?${params}`)
  } catch (e: any) {
    ElMessage.error('加载价格策略失败')
  } finally {
    strategiesLoading.value = false
  }
}

async function onStrategySelect(row: Strategy | null) {
  selectedStrategy.value = row
  prices.value = []
  if (!row) return
  pricesLoading.value = true
  try {
    prices.value = await api.get<Price[]>(`/vms/api/vmPrice/strategy/${row.id}/detail`)
  } catch (e: any) {
    ElMessage.error('加载策略明细失败')
  } finally {
    pricesLoading.value = false
  }
}

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

async function applyPrice() {
  if (!selectedStrategy.value || selectedVmIds.value.length === 0) return
  applying.value = true
  try {
    const formData = new FormData()
    formData.append('strategyId', selectedStrategy.value.id.toString())
    formData.append('vmIds', selectedVmIds.value.join(','))
    const res = await api.post<any>(`/vms/api/vmPrice/apply`, formData)
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '应用成功')
    } else {
      ElMessage.error(res.message || '应用失败')
    }
  } catch (e: any) {
    ElMessage.error('应用失败')
  } finally {
    applying.value = false
  }
}

onMounted(() => loadStrategies())
</script>

<style scoped>
.price-config h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.layout { display: flex; gap: 16px; align-items: flex-start; }
.panel { background: white; border-radius: 4px; padding: 16px; flex: 1; min-width: 0; }
.right-panel { flex: 1.5; }
.panel-header { font-weight: 600; margin-bottom: 12px; }
.empty-tip { color: #95a5a6; text-align: center; padding: 40px 0; }
</style>
