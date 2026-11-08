<template>
  <div class="shortcuts-config">
    <h1>设备闪购键配置</h1>
    <p class="hint">查看和编辑设备闪购键（键位、绑定商品）。</p>

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
          @change="loadShortcuts"
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
        <el-button type="primary" @click="saveAll" :disabled="!selectedVmId || shortcuts.length === 0" :loading="saving">
          保存全部
        </el-button>
      </el-form-item>
    </el-form>

    <div v-if="!selectedVmId" class="empty-tip">请先选择设备</div>
    <template v-else>
      <el-table :data="shortcuts" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="键位码" width="120">
          <template #default="scope">
            <el-input v-model="scope.row.shortcutsCode" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="绑定商品" min-width="200">
          <template #default="scope">
            <el-select v-model="scope.row.skuId" filterable remote :remote-method="(q: string) => searchSkus(q, scope.row)" placeholder="搜索商品" size="small" style="width: 100%">
              <el-option v-for="s in scope.row._skuOptions || []" :key="s.id" :label="`${s.skuName} (${s.skuCode || ''})`" :value="s.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="绑定类型" width="100">
          <template #default="scope">
            <el-input-number v-model="scope.row.bindType" :min="0" size="small" style="width: 100%" />
          </template>
        </el-table-column>
      </el-table>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface Shortcut {
  id: number
  shortcutsCode: string
  bindType: number
  skuId: number | null
  skuName: string
  resourceId: number
  _skuOptions?: SkuOption[]
}

interface SkuOption {
  id: number
  skuName: string
  skuCode: string
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

const shortcuts = ref<Shortcut[]>([])
const loading = ref(false)
const saving = ref(false)

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

async function loadShortcuts() {
  if (!selectedVmId.value) return
  loading.value = true
  try {
    const list = await api.get<Shortcut[]>(`/vms/api/vmShortcuts/${selectedVmId.value}/list`)
    shortcuts.value = list.map((s) => ({ ...s, _skuOptions: s.skuId ? [{ id: s.skuId, skuName: s.skuName || '', skuCode: '' }] : [] }))
  } catch (e: any) {
    ElMessage.error('加载闪购键失败')
  } finally {
    loading.value = false
  }
}

async function searchSkus(query: string, row: Shortcut) {
  if (!query) {
    row._skuOptions = []
    return
  }
  try {
    const res = await api.get<{ total: number; data: SkuOption[] }>(
      `/vms/api/sku/list?page=1&pageSize=20&name=${encodeURIComponent(query)}`,
    )
    row._skuOptions = res.data
  } catch (e) {
    ElMessage.error('搜索商品失败')
  }
}

async function saveAll() {
  if (!selectedVmId.value) return
  saving.value = true
  try {
    const formData = new FormData()
    formData.append('ids', shortcuts.value.map((s) => s.id).join(','))
    formData.append('skuIds', shortcuts.value.map((s) => s.skuId ?? '').join(','))
    formData.append('shortcutsCodes', shortcuts.value.map((s) => s.shortcutsCode || '').join(','))
    formData.append('bindTypes', shortcuts.value.map((s) => s.bindType ?? 0).join(','))

    const res = await api.post<any>(`/vms/api/vmShortcuts/${selectedVmId.value}/save`, formData)
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '保存成功')
      loadShortcuts()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  const vmIdParam = route.query.vmId
  if (vmIdParam) {
    selectedVmId.value = Number(vmIdParam)
    loadShortcuts()
  }
})
</script>

<style scoped>
.shortcuts-config h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { margin-bottom: 16px; padding: 16px; background: white; border-radius: 4px; }
.empty-tip { color: #95a5a6; text-align: center; padding: 40px 0; }
</style>
