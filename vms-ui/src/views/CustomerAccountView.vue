<template>
  <div class="pay-account">
    <h1>支付管理</h1>
    <p class="hint">维护客户收款账户（收款账号 / 转账账号）。原短信验证流程已随短信网关停用。</p>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="customerName" label="客户名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="managerName" label="负责人" width="100" />
      <el-table-column prop="managerPhone" label="电话" width="130" />
      <el-table-column prop="collectionAccount" label="收款账号" min-width="150" show-overflow-tooltip />
      <el-table-column prop="collectionAccountType" label="收款类型" width="100" />
      <el-table-column prop="giroAccount" label="转账账号" min-width="150" show-overflow-tooltip />
      <el-table-column prop="giroAccountName" label="转账户名" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="onPageChange" />

    <el-dialog v-model="editorVisible" :title="`收款账户：${current?.customerName || ''}`" width="520px">
      <el-form :model="editor" label-width="100px">
        <el-form-item label="收款账号"><el-input v-model="editor.collectionAccount" maxlength="60" /></el-form-item>
        <el-form-item label="收款类型">
          <el-select v-model="editor.collectionAccountType" clearable placeholder="选择类型" style="width: 200px">
            <el-option v-for="t in accountTypes" :key="t.id" :label="t.name" :value="t.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="转账账号"><el-input v-model="editor.giroAccount" maxlength="60" /></el-form-item>
        <el-form-item label="转账户名"><el-input v-model="editor.giroAccountName" maxlength="60" /></el-form-item>
        <el-form-item label="转账账号类型">
          <el-select v-model="editor.giroAccountType" clearable placeholder="选择类型" style="width: 200px">
            <el-option v-for="t in accountTypes" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface PayRow {
  id: number
  customerName: string
  managerName: string | null
  managerPhone: string | null
  collectionAccount: string | null
  collectionAccountType: string | null
  giroAccount: string | null
  giroAccountName: string | null
  giroAccountType: number | null
}

const rows = ref<PayRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const accountTypes = ref<{ id: number; name: string }[]>([])

const editorVisible = ref(false)
const saving = ref(false)
const current = ref<PayRow | null>(null)
const editor = ref<{
  ids?: number
  collectionAccount: string
  collectionAccountType: string | undefined
  giroAccount: string
  giroAccountName: string
  giroAccountType: number | undefined
}>({ collectionAccount: '', collectionAccountType: undefined, giroAccount: '', giroAccountName: '', giroAccountType: undefined })

async function loadTypes() {
  try {
    accountTypes.value = await api.get<{ id: number; name: string }[]>('/vms/api/customerAccount/options')
  } catch (e) {
    // 字典缺失不阻塞
  }
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    const res = await api.get<{ total: number; data: PayRow[] }>(`/vms/api/customerAccount/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载支付账户失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function openEdit(row: PayRow) {
  current.value = row
  editor.value = {
    ids: row.id,
    collectionAccount: row.collectionAccount || '',
    collectionAccountType: row.collectionAccountType || undefined,
    giroAccount: row.giroAccount || '',
    giroAccountName: row.giroAccountName || '',
    giroAccountType: row.giroAccountType ?? undefined,
  }
  editorVisible.value = true
}

async function save() {
  saving.value = true
  try {
    const res = await api.post<any>('/vms/api/customerAccount/update', { ...editor.value }, { form: true })
    if (res?.code === 0) {
      ElMessage.success('保存成功')
      editorVisible.value = false
      loadList()
    } else {
      ElMessage.error(res?.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadTypes()
  loadList()
})
</script>

<style scoped>
.pay-account h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
