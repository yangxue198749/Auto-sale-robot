<template>
  <div class="channel-config">
    <h1>设备货道配置</h1>
    <p class="hint">查看设备货道、关联商品。</p>

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
          @change="loadChannels"
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
        <el-button type="primary" @click="loadChannels" :disabled="!selectedVmId">刷新货道</el-button>
      </el-form-item>
    </el-form>

    <div v-if="!selectedVmId" class="empty-tip">请先选择设备</div>
    <template v-else>
      <el-table :data="channels" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="channelCode" label="货道编号" width="110" />
        <el-table-column prop="skuName" label="商品" min-width="160">
          <template #default="scope">
            <span v-if="scope.row.skuName">{{ scope.row.skuName }} ({{ scope.row.skuCode }})</span>
            <span v-else style="color: #95a5a6">未关联</span>
          </template>
        </el-table-column>
        <el-table-column prop="currNum" label="当前库存" width="100" />
        <el-table-column prop="maxCapacity" label="最大容量" width="100" />
        <el-table-column prop="channelStatus" label="状态" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.channelStatus === 0 ? 'success' : 'info'" size="small">
              {{ scope.row.channelStatus === 0 ? '正常' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="showRelateDialog(scope.row)">关联商品</el-button>
            <el-button
              v-if="scope.row.skuId"
              type="danger"
              link
              @click="cancelRelate(scope.row)"
            >
              取消关联
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 关联商品对话框 -->
    <el-dialog v-model="relateVisible" title="关联商品" width="500px">
      <el-form label-width="90px">
        <el-form-item label="货道">
          <el-input :value="relateForm.channelCode" disabled />
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="relateForm.skuId" filterable remote :remote-method="searchSkus" :loading="skuLoading" placeholder="搜索商品名称">
            <el-option v-for="s in skuOptions" :key="s.id" :label="`${s.skuName} (${s.skuCode || ''})`" :value="s.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="relateVisible = false">取消</el-button>
        <el-button type="primary" :loading="relating" @click="submitRelate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface VmOption {
  id: number
  innerCode: string
  vmDesc: string
}

interface Channel {
  id: number
  channelCode: string
  channelStatus: number
  currNum: number
  maxCapacity: number
  skuId: number
  skuName: string
  skuCode: string
}

interface SkuOption {
  id: number
  skuName: string
  skuCode: string
}

const route = useRoute()
const selectedVmId = ref<number | undefined>(undefined)
const vmOptions = ref<VmOption[]>([])
const vmLoading = ref(false)

const channels = ref<Channel[]>([])
const loading = ref(false)

const relateVisible = ref(false)
const relateForm = ref({ channelId: 0, channelCode: '', skuId: undefined as number | undefined })
const skuOptions = ref<SkuOption[]>([])
const skuLoading = ref(false)
const relating = ref(false)

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

async function loadChannels() {
  if (!selectedVmId.value) return
  loading.value = true
  try {
    channels.value = await api.get<Channel[]>(`/vms/api/vmChannel/${selectedVmId.value}/list`)
  } catch (e: any) {
    ElMessage.error('加载货道失败')
  } finally {
    loading.value = false
  }
}

function showRelateDialog(row: Channel) {
  relateForm.value = { channelId: row.id, channelCode: row.channelCode, skuId: row.skuId || undefined }
  relateVisible.value = true
}

async function searchSkus(query: string) {
  if (!query) {
    skuOptions.value = []
    return
  }
  skuLoading.value = true
  try {
    const res = await api.get<{ total: number; data: SkuOption[] }>(
      `/vms/api/sku/list?page=1&pageSize=20&name=${encodeURIComponent(query)}`,
    )
    skuOptions.value = res.data
  } catch (e) {
    ElMessage.error('搜索商品失败')
  } finally {
    skuLoading.value = false
  }
}

async function submitRelate() {
  if (!relateForm.value.skuId || !selectedVmId.value) {
    ElMessage.warning('请选择商品')
    return
  }
  relating.value = true
  try {
    const formData = new FormData()
    formData.append('channelIds', relateForm.value.channelId.toString())
    formData.append('skuId', relateForm.value.skuId.toString())
    const res = await api.post<any>(`/vms/api/vmChannel/${selectedVmId.value}/relateSku`, formData)
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '关联成功')
      relateVisible.value = false
      loadChannels()
    } else {
      ElMessage.error(res.message || '关联失败')
    }
  } catch (e: any) {
    ElMessage.error('关联失败')
  } finally {
    relating.value = false
  }
}

async function cancelRelate(row: Channel) {
  if (!selectedVmId.value) return
  try {
    await ElMessageBox.confirm(`确定取消货道「${row.channelCode}」的商品关联吗？`, '确认取消', { type: 'warning' })
  } catch {
    return
  }
  try {
    const formData = new FormData()
    formData.append('channelIds', row.id.toString())
    const res = await api.post<any>(`/vms/api/vmChannel/${selectedVmId.value}/cancelRelate`, formData)
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '取消关联成功')
      loadChannels()
    } else {
      ElMessage.error(res.message || '取消关联失败')
    }
  } catch (e: any) {
    ElMessage.error('取消关联失败')
  }
}

onMounted(() => {
  const vmIdParam = route.query.vmId
  if (vmIdParam) {
    selectedVmId.value = Number(vmIdParam)
    loadChannels()
  }
})
</script>

<style scoped>
.channel-config h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { margin-bottom: 16px; padding: 16px; background: white; border-radius: 4px; }
.empty-tip { color: #95a5a6; text-align: center; padding: 40px 0; }
</style>
