<template>
  <div class="text-config">
    <h1>设备走马灯配置</h1>
    <p class="hint">配置设备屏幕走马灯文字及生效时间。</p>

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
          @change="loadTexts"
        >
          <el-option v-for="vm in vmOptions" :key="vm.id" :label="`${vm.innerCode} - ${vm.vmDesc || '未命名'}`" :value="vm.id" />
        </el-select>
      </el-form-item>
    </el-form>

    <div v-if="!selectedVmId" class="empty-tip">请先选择设备</div>
    <template v-else>
      <!-- 当前配置 -->
      <el-card shadow="never" style="margin-bottom: 16px">
        <template #header>当前走马灯配置</template>
        <el-table :data="texts" v-loading="loading" size="small" border style="width: 100%">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="textContents" label="内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="beginDate" label="开始日期" width="180">
            <template #default="scope">{{ formatDate(scope.row.beginDate) }}</template>
          </el-table-column>
          <el-table-column prop="endDate" label="结束日期" width="180">
            <template #default="scope">{{ formatDate(scope.row.endDate) }}</template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 编辑配置 -->
      <el-card shadow="never">
        <template #header>设置走马灯</template>
        <el-form :model="form" label-width="90px">
          <el-form-item label="文字内容">
            <el-input v-model="form.mtText" type="textarea" :rows="4" placeholder="请输入走马灯文字内容" maxlength="2000" show-word-limit />
          </el-form-item>
          <el-form-item label="开始日期">
            <el-date-picker v-model="form.beginDate" type="date" value-format="yyyy-MM-dd" placeholder="开始日期" />
          </el-form-item>
          <el-form-item label="结束日期">
            <el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" placeholder="结束日期" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="saveText">保存并应用</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface VmText {
  id: number
  textContents: string
  beginDate: string
  endDate: string
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

const texts = ref<VmText[]>([])
const loading = ref(false)
const saving = ref(false)

const form = ref({
  mtText: '',
  beginDate: '',
  endDate: '',
})

function formatDate(dateStr: string | null) {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').substring(0, 19)
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

async function loadTexts() {
  if (!selectedVmId.value) return
  loading.value = true
  try {
    texts.value = await api.get<VmText[]>(`/vms/api/vmText/${selectedVmId.value}/list`)
  } catch (e: any) {
    ElMessage.error('加载走马灯配置失败')
  } finally {
    loading.value = false
  }
}

async function saveText() {
  if (!selectedVmId.value) return
  if (!form.value.mtText) {
    ElMessage.warning('请输入走马灯文字内容')
    return
  }
  saving.value = true
  try {
    const formData = new FormData()
    formData.append('mtText', form.value.mtText)
    if (form.value.beginDate) formData.append('beginDate', form.value.beginDate)
    if (form.value.endDate) formData.append('endDate', form.value.endDate)

    const res = await api.post<any>(`/vms/api/vmText/${selectedVmId.value}/save`, formData)
    if (res.success || res.type === 'success') {
      ElMessage.success(res.message || '保存成功')
      loadTexts()
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
    loadTexts()
  }
})
</script>

<style scoped>
.text-config h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { margin-bottom: 16px; padding: 16px; background: white; border-radius: 4px; }
.empty-tip { color: #95a5a6; text-align: center; padding: 40px 0; }
</style>
