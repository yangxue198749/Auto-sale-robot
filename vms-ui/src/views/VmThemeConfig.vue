<template>
  <div class="theme-config">
    <h1>设备皮肤配置</h1>
    <p class="hint">为设备绑定皮肤主题、设置版式，并配置三个入口跳转页。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="设备">
        <el-select
          v-model="selectedVmIds"
          multiple
          filterable
          remote
          :remote-method="searchVms"
          :loading="vmLoading"
          placeholder="搜索设备编号（可多选）"
          style="width: 380px"
          @change="onVmChange"
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
        <span class="selected-tip">已选 {{ selectedVmIds.length }} 台设备，配置将同时下发到所有选中设备</span>
      </el-form-item>
    </el-form>

    <div v-if="!selectedVmIds.length" class="empty-tip">请先选择设备</div>
    <el-tabs v-else v-model="activeTab">
      <!-- 选择皮肤 -->
      <el-tab-pane label="选择皮肤" name="theme">
        <el-card shadow="never">
          <el-form :inline="true">
            <el-form-item label="策略名称">
              <el-input v-model="themeName" placeholder="请输入皮肤名称" clearable @keyup.enter="loadThemes" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="themeLoading" @click="loadThemes">查询</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="themes" v-loading="themeLoading" border stripe highlight-current-row style="width: 100%" @current-change="onThemeRowChange">
            <el-table-column width="60" align="center">
              <template #default="scope">
                <el-radio v-model="currentThemeId" :value="scope.row.id" />
              </template>
            </el-table-column>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="themeName" label="皮肤名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="themeDesc" label="描述" min-width="200" show-overflow-tooltip />
          </el-table>

          <div class="actions">
            <el-button type="primary" :disabled="!currentThemeId" :loading="binding" @click="bindTheme">绑定到选中设备</el-button>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 选择版式 -->
      <el-tab-pane label="选择版式" name="format">
        <el-card shadow="never">
          <el-radio-group v-model="formatCode">
            <el-radio :value="2" border style="margin-right: 16px">
              2 页版式（商品列表页、商品支付页）
            </el-radio>
            <el-radio :value="3" border>
              3 页版式（商品走马灯页、商品列表页、商品支付页）
            </el-radio>
          </el-radio-group>
          <div class="actions">
            <el-button type="primary" :loading="formatting" @click="saveFormat">应用版式</el-button>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 入口配置 -->
      <el-tab-pane label="入口配置" name="entrance">
        <el-card v-if="selectedVmIds.length === 1" shadow="never" style="margin-bottom: 16px">
          <template #header>设备 {{ selectedVmIds[0] }} 当前入口配置</template>
          <el-table :data="entrances" v-loading="entranceLoading" size="small" border style="width: 100%">
            <el-table-column prop="entranceIndex" label="入口" width="80" />
            <el-table-column prop="resourceName" label="跳转页" min-width="180" show-overflow-tooltip />
            <el-table-column prop="imgUrl" label="图片" min-width="220" show-overflow-tooltip />
            <el-table-column v-if="hasEntranceImage" label="预览" width="120">
              <template #default="scope">
                <img v-if="scope.row.imgFullUrl" :src="scope.row.imgFullUrl" class="entrance-img" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        <div v-else class="hint-inline">多选设备时，当前入口配置请逐台查看；下方设置会应用到全部选中设备。</div>

        <el-card shadow="never">
          <template #header>设置入口（应用到全部选中设备）</template>
          <el-table :data="portalForms" size="small" border style="width: 100%">
            <el-table-column label="入口" width="80">
              <template #default="scope">{{ scope.row.index }}</template>
            </el-table-column>
            <el-table-column label="跳转页" min-width="220">
              <template #default="scope">
                <el-select
                  v-model="scope.row.resourceId"
                  filterable
                  remote
                  clearable
                  :remote-method="searchResources"
                  :loading="resourceLoading"
                  placeholder="搜索跳转页策略"
                  size="small"
                  style="width: 100%"
                >
                  <el-option v-for="r in resourceOptions" :key="r.id" :label="r.resourceName" :value="r.id" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="图片（≤100KB，png/jpg）" min-width="240">
              <template #default="scope">
                <input type="file" accept=".png,.jpg,.jpeg" class="file-input" @change="onFileChange(scope.row, $event)" />
                <span v-if="scope.row.fileName" class="file-name">{{ scope.row.fileName }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="90">
              <template #default="scope">
                <el-button link type="danger" size="small" @click="clearPortal(scope.row)">清空</el-button>
              </template>
            </el-table-column>
          </el-table>
          <p class="hint">跳转页留空即清除该入口；未重新上传图片时保留原图。</p>
          <div class="actions">
            <el-button type="primary" :loading="savingEntrance" @click="saveConfiguration">保存入口配置</el-button>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface VmOption {
  id: number
  innerCode: string
  vmDesc: string
}

interface ThemeRow {
  id: number
  themeName: string
  themeDesc: string
  themeUrl: string
  previewUrls: string
}

interface ResourceRow {
  id: number
  resourceName: string
  resourceDesc: string
}

interface EntranceRow {
  id: number
  entranceIndex: number
  resourceId: number | null
  resourceName: string
  imgUrl: string
  imgFullUrl: string
}

interface PortalForm {
  index: number
  resourceId: number | undefined
  file: File | null
  fileName: string
}

const selectedVmIds = ref<number[]>([])
const vmOptions = ref<VmOption[]>([])
const vmLoading = ref(false)
const activeTab = ref('theme')

const themeName = ref('')
const themes = ref<ThemeRow[]>([])
const themeLoading = ref(false)
const currentThemeId = ref<number | undefined>(undefined)
const binding = ref(false)

const formatCode = ref(3)
const formatting = ref(false)

const entrances = ref<EntranceRow[]>([])
const entranceLoading = ref(false)
const hasEntranceImage = computed(() => entrances.value.some((e) => !!e.imgFullUrl))

const portalForms = ref<PortalForm[]>([
  { index: 1, resourceId: undefined, file: null, fileName: '' },
  { index: 2, resourceId: undefined, file: null, fileName: '' },
  { index: 3, resourceId: undefined, file: null, fileName: '' },
])
const resourceOptions = ref<ResourceRow[]>([])
const resourceLoading = ref(false)
const savingEntrance = ref(false)

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

function onVmChange() {
  currentThemeId.value = undefined
  loadEntrances()
}

async function loadThemes() {
  themeLoading.value = true
  try {
    const query = themeName.value ? `?themeName=${encodeURIComponent(themeName.value)}` : ''
    themes.value = await api.get<ThemeRow[]>(`/vms/api/vmTheme/themes${query}`)
  } catch (e) {
    ElMessage.error('加载皮肤列表失败')
  } finally {
    themeLoading.value = false
  }
}

function onThemeRowChange(row: ThemeRow | null) {
  if (row) currentThemeId.value = row.id
}

async function bindTheme() {
  if (!currentThemeId.value || !selectedVmIds.value.length) return
  binding.value = true
  try {
    const res = await api.post<any>('/vms/api/vmTheme/bind', {
      themeId: currentThemeId.value,
      vmIds: selectedVmIds.value.join(','),
    }, { form: true })
    handleResult(res, '皮肤绑定成功')
  } catch (e) {
    ElMessage.error('绑定失败')
  } finally {
    binding.value = false
  }
}

async function saveFormat() {
  if (!selectedVmIds.value.length) return
  formatting.value = true
  try {
    const res = await api.post<any>('/vms/api/vmTheme/format', {
      formatCode: formatCode.value,
      vmIds: selectedVmIds.value.join(','),
    }, { form: true })
    handleResult(res, '版式设置成功')
  } catch (e) {
    ElMessage.error('设置版式失败')
  } finally {
    formatting.value = false
  }
}

async function loadEntrances() {
  portalForms.value.forEach((p) => {
    p.resourceId = undefined
    p.file = null
    p.fileName = ''
  })
  if (selectedVmIds.value.length !== 1) {
    entrances.value = []
    return
  }
  entranceLoading.value = true
  try {
    entrances.value = await api.get<EntranceRow[]>(`/vms/api/vmTheme/${selectedVmIds.value[0]}/entrances`)
    for (const p of portalForms.value) {
      const existing = entrances.value.find((e) => e.entranceIndex === p.index)
      if (existing?.resourceId) p.resourceId = existing.resourceId
    }
  } catch (e) {
    ElMessage.error('加载入口配置失败')
  } finally {
    entranceLoading.value = false
  }
}

async function searchResources(query: string) {
  resourceLoading.value = true
  try {
    const q = query ? `?name=${encodeURIComponent(query)}` : ''
    resourceOptions.value = await api.get<ResourceRow[]>(`/vms/api/vmTheme/resources${q}`)
  } catch (e) {
    ElMessage.error('加载跳转页资源失败')
  } finally {
    resourceLoading.value = false
  }
}

function onFileChange(portal: PortalForm, event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0] || null
  if (!file) {
    portal.file = null
    portal.fileName = ''
    return
  }
  const lower = file.name.toLowerCase()
  if (!lower.endsWith('.png') && !lower.endsWith('.jpg') && !lower.endsWith('.jpeg')) {
    ElMessage.warning('仅支持 png/jpg/jpeg 图片')
    input.value = ''
    return
  }
  if (file.size > 100 * 1024) {
    ElMessage.warning('图片不能大于 100KB')
    input.value = ''
    return
  }
  portal.file = file
  portal.fileName = file.name
}

function clearPortal(portal: PortalForm) {
  portal.resourceId = undefined
  portal.file = null
  portal.fileName = ''
}

async function saveConfiguration() {
  if (!selectedVmIds.value.length) return
  savingEntrance.value = true
  try {
    const formData = new FormData()
    formData.append('vmIds', selectedVmIds.value.join(','))
    portalForms.value.forEach((p, i) => {
      const key = ['firstResourceId', 'secondResourceId', 'thirdResourceId'][i]
      if (p.resourceId) formData.append(key, String(p.resourceId))
      if (p.file) {
        const fileKey = ['firstFile', 'secondFile', 'thirdFile'][i]
        formData.append(fileKey, p.file)
      }
    })
    const res = await api.upload<any>('/vms/api/vmTheme/configuration', formData)
    handleResult(res, '入口配置保存成功')
    loadEntrances()
  } catch (e) {
    ElMessage.error('保存入口配置失败')
  } finally {
    savingEntrance.value = false
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
  loadThemes()
  searchResources('')
})
</script>

<style scoped>
.theme-config h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.hint-inline { color: #7f8c8d; font-size: 12px; margin-top: 8px; }
.filter-form { margin-bottom: 8px; padding: 16px; background: white; border-radius: 4px; }
.selected-tip { color: #7f8c8d; font-size: 13px; }
.empty-tip { color: #95a5a6; text-align: center; padding: 40px 0; }
.actions { margin-top: 16px; }
.file-input { font-size: 12px; }
.file-name { margin-left: 8px; color: #27ae60; font-size: 12px; }
.entrance-img { width: 64px; height: 32px; object-fit: contain; border: 1px solid #eee; }
</style>
