<template>
  <div class="company-manage">
    <h1>公司管理</h1>
    <p class="hint">管理下属公司（新建/改名/分配售货机额度/升级），层级权限由公司级别决定。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="筛选">
        <el-select v-model="filterType" style="width: 120px">
          <el-option label="公司名称" value="1" />
          <el-option label="客户名称" value="2" />
          <el-option label="管理员" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="filterText" placeholder="关键字" clearable style="width: 150px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
        <el-button v-if="me?.canCreateCompany" type="success" @click="openCreate">新建公司</el-button>
        <el-button v-if="me?.canUpgrade" type="warning" @click="openUpgrade">升级公司</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="companyName" label="公司名称" min-width="170" show-overflow-tooltip />
      <el-table-column label="级别" width="80">
        <template #default="scope">{{ levelText(scope.row.companyLevel) }}</template>
      </el-table-column>
      <el-table-column prop="customerName" label="客户" min-width="130" show-overflow-tooltip />
      <el-table-column prop="parentCompanyName" label="上级公司" min-width="130" show-overflow-tooltip />
      <el-table-column prop="maxVmCapacity" label="售货机额度" width="100" />
      <el-table-column label="创建时间" min-width="150">
        <template #default="scope">{{ formatTime(scope.row.ctime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openRename(scope.row)">改名</el-button>
          <el-button v-if="me && scope.row.id !== me.id && me.companyLevel === 1" link type="primary" @click="openCapacity(scope.row)">分配额度</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="onPageChange" />

    <el-dialog v-model="createVisible" title="新建公司" width="520px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="公司名称" required><el-input v-model="createForm.companyName" maxlength="80" /></el-form-item>
        <el-form-item label="关联客户" required>
          <el-select v-model="createForm.customerId" placeholder="选择未被关联的客户" style="width: 100%">
            <el-option v-for="c in unusedCustomers" :key="c.id" :label="c.customerName" :value="String(c.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理员姓名" required><el-input v-model="createForm.adminName" maxlength="40" /></el-form-item>
        <el-form-item label="管理员账号" required><el-input v-model="createForm.adminAccount" maxlength="40" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="createForm.userPhone" maxlength="20" /></el-form-item>
        <el-form-item label="售货机额度"><el-input-number v-model="createForm.maxVmCapacity" :min="0" :max="me?.maxVmCapacity || 0" controls-position="right" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="create">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="renameVisible" title="公司改名" width="420px">
      <el-form label-width="90px">
        <el-form-item label="公司名称" required><el-input v-model="renameForm.companyName" maxlength="80" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renameVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="rename">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="capacityVisible" title="分配售货机额度" width="440px">
      <p class="hint">已使用：{{ capacityForm.used }} 台；新额度不能小于已使用数量。</p>
      <el-form label-width="90px">
        <el-form-item label="新额度">
          <el-input-number v-model="capacityForm.newVmCapacitySum" :min="0" :max="(me?.maxVmCapacity || 0) + capacityForm.old" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="capacityVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveCapacity">分配</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="upgradeVisible" title="升级公司（三级 → 二级）" width="460px">
      <el-form :model="upgradeForm" label-width="100px">
        <el-form-item label="公司名称" required><el-input v-model="upgradeForm.companyName" maxlength="80" /></el-form-item>
        <el-form-item label="新管理员" required>
          <el-select v-model="upgradeForm.newAdminUserId" placeholder="选择本公司其他用户" style="width: 100%">
            <el-option v-for="u in upgradeCandidates" :key="u.id" :label="`${u.userName} (${u.userAccount})`" :value="String(u.id)" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="upgradeVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="upgrade">升级</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface CompanyRow {
  id: number
  companyName: string
  companyLevel: number | null
  customerName?: string
  parentCompanyName?: string
  maxVmCapacity: number | null
  ctime: string | null
}

interface Me {
  id: number
  companyName: string
  companyLevel: number
  maxVmCapacity: number | null
  canCreateCompany: boolean
  canUpgrade: boolean
}

const rows = ref<CompanyRow[]>([])
const me = ref<Me | null>(null)
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterType = ref('1')
const filterText = ref('')

const unusedCustomers = ref<{ id: number; customerName: string }[]>([])
const upgradeCandidates = ref<{ id: number; userName: string; userAccount: string }[]>([])

const createVisible = ref(false)
const renameVisible = ref(false)
const capacityVisible = ref(false)
const upgradeVisible = ref(false)
const saving = ref(false)

const createForm = ref<{ companyName: string; customerId: string | undefined; adminName: string; adminAccount: string; userPhone: string; maxVmCapacity: number | undefined }>({
  companyName: '', customerId: undefined, adminName: '', adminAccount: '', userPhone: '', maxVmCapacity: undefined,
})
const renameForm = ref<{ id?: number; companyName: string }>({ companyName: '' })
const capacityForm = ref<{ id?: number; used: number; old: number; newVmCapacitySum: number | undefined }>({ used: 0, old: 0, newVmCapacitySum: undefined })
const upgradeForm = ref<{ companyName: string; newAdminUserId: string | undefined }>({ companyName: '', newAdminUserId: undefined })

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

function levelText(level: number | null) {
  if (level === 1) return '一级'
  if (level === 2) return '二级'
  if (level === 3) return '三级'
  return '-'
}

async function loadMe() {
  try {
    me.value = await api.get<Me>('/vms/api/company/me')
  } catch (e) {
    // ignore
  }
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterText.value) {
      params.set('searchType', filterType.value)
      params.set('searchName', filterText.value)
    }
    const res = await api.get<{ total: number; data: CompanyRow[] }>(`/vms/api/company/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载公司列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

async function openCreate() {
  createForm.value = { companyName: '', customerId: undefined, adminName: '', adminAccount: '', userPhone: '', maxVmCapacity: undefined }
  createVisible.value = true
  try {
    unusedCustomers.value = await api.get('/vms/api/company/unusedCustomers')
  } catch (e) {
    ElMessage.error('加载可选客户失败')
  }
}

async function create() {
  const f = createForm.value
  if (!f.companyName || !f.customerId || !f.adminName || !f.adminAccount) {
    ElMessage.warning('请填写完整')
    return
  }
  saving.value = true
  try {
    const res = await api.post<any>('/vms/api/company/create', { ...f }, { form: true })
    handleResult(res, '创建成功')
    if (res?.code === 0 || res?.type === 'success') {
      createVisible.value = false
      loadList()
      loadMe()
    }
  } catch (e) {
    ElMessage.error('创建失败')
  } finally {
    saving.value = false
  }
}

function openRename(row: CompanyRow) {
  renameForm.value = { id: row.id, companyName: row.companyName }
  renameVisible.value = true
}

async function rename() {
  saving.value = true
  try {
    const res = await api.post<any>('/vms/api/company/update', { ...renameForm.value }, { form: true })
    handleResult(res, '保存成功')
    if (res?.code === 0) {
      renameVisible.value = false
      loadList()
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function openCapacity(row: CompanyRow) {
  capacityForm.value = {
    id: row.id,
    used: 0,
    old: row.maxVmCapacity ?? 0,
    newVmCapacitySum: row.maxVmCapacity ?? 0,
  }
  capacityVisible.value = true
  try {
    const detail = await api.get<CompanyRow>(`/vms/api/company/list?page=1&pageSize=1&searchType=1&searchName=${encodeURIComponent(row.companyName)}`)
    // 已使用数量由后端校验，这里仅展示额度
  } catch (e) {
    // ignore
  }
}

async function saveCapacity() {
  const f = capacityForm.value
  if (f.newVmCapacitySum === undefined) return
  saving.value = true
  try {
    const res = await api.post<any>('/vms/api/company/addCapacity', {
      ids: f.id,
      newVmCapacitySum: f.newVmCapacitySum,
    }, { form: true })
    handleResult(res, '分配成功')
    if (res?.code === 0) {
      capacityVisible.value = false
      loadList()
      loadMe()
    }
  } catch (e) {
    ElMessage.error('分配失败')
  } finally {
    saving.value = false
  }
}

async function openUpgrade() {
  upgradeForm.value = { companyName: me.value?.companyName || '', newAdminUserId: undefined }
  upgradeVisible.value = true
  try {
    upgradeCandidates.value = await api.get('/vms/api/company/upgradeCandidates')
  } catch (e) {
    ElMessage.error('加载候选管理员失败')
  }
}

async function upgrade() {
  const f = upgradeForm.value
  if (!f.companyName || !f.newAdminUserId) {
    ElMessage.warning('请填写完整')
    return
  }
  saving.value = true
  try {
    const res = await api.post<any>('/vms/api/company/upgrade', { ...f }, { form: true })
    handleResult(res, '升级成功')
    if (res?.code === 0 || res?.type === 'success') {
      upgradeVisible.value = false
      loadList()
      loadMe()
    }
  } catch (e) {
    ElMessage.error('升级失败')
  } finally {
    saving.value = false
  }
}

function handleResult(res: any, successMessage: string) {
  if (res?.code === 0 || res?.type === 'success' || res?.success) {
    ElMessage.success(res?.message || successMessage)
  } else {
    ElMessage.error(res?.message || '操作失败')
  }
}

onMounted(() => {
  loadMe()
  loadList()
})
</script>

<style scoped>
.company-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 8px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
