<template>
  <div class="user-manage">
    <h1>用户管理</h1>
    <p class="hint">维护公司用户、角色绑定与密码重置。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="筛选">
        <el-select v-model="filterType" style="width: 120px">
          <el-option label="按姓名" value="1" />
          <el-option label="按公司" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="filterText" placeholder="关键字" clearable style="width: 180px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openCreate">新建用户</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="deleteSelected">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="userAccount" label="账号" min-width="120" />
      <el-table-column prop="userName" label="姓名" min-width="100" />
      <el-table-column prop="userPhone" label="电话" min-width="120" />
      <el-table-column prop="companyName" label="公司" min-width="150" show-overflow-tooltip />
      <el-table-column label="角色" min-width="160">
        <template #default="scope">
          <el-tag v-for="name in scope.row.roleNames" :key="name" size="small" style="margin-right: 4px">{{ name }}</el-tag>
          <span v-if="!scope.row.roleNames?.length">-</span>
        </template>
      </el-table-column>
      <el-table-column label="最近登录" min-width="150">
        <template #default="scope">{{ formatTime(scope.row.loginTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button link type="warning" @click="resetPwd(scope.row)">重置密码</el-button>
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

    <el-dialog v-model="editorVisible" :title="editorMode === 'create' ? '新建用户' : '编辑用户'" width="480px">
      <el-form :model="editor" label-width="90px">
        <el-form-item v-if="editorMode === 'create'" label="账号" required>
          <el-input v-model="editor.userAccount" maxlength="40" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="editor.userName" maxlength="40" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="editor.userPhone" maxlength="20" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editor.roleIds" multiple placeholder="选择角色" style="width: 100%">
            <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface UserRow {
  id: number
  userAccount: string
  userName: string
  userPhone: string | null
  companyName: string | null
  roleIds: number[]
  roleNames: string[]
  loginTime: string | null
}

interface RoleOption {
  id: number
  roleName: string
}

const rows = ref<UserRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterType = ref('1')
const filterText = ref('')
const selectedRows = ref<UserRow[]>([])

const roleOptions = ref<RoleOption[]>([])
const editorVisible = ref(false)
const editorMode = ref<'create' | 'edit'>('create')
const saving = ref(false)
const editor = ref<{
  userId?: number
  userAccount: string
  userName: string
  userPhone: string
  roleIds: number[]
}>({ userAccount: '', userName: '', userPhone: '', roleIds: [] })

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

async function loadRoles() {
  try {
    roleOptions.value = await api.get<RoleOption[]>('/vms/api/sysUser/roleOptions')
  } catch (e) {
    // 角色加载失败不阻塞
  }
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterText.value) {
      params.set('selectType', filterType.value)
      params.set('selectText', filterText.value)
    }
    const res = await api.get<{ total: number; data: UserRow[] }>(`/vms/api/sysUser/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: UserRow[]) {
  selectedRows.value = selection
}

function openCreate() {
  editorMode.value = 'create'
  editor.value = { userAccount: '', userName: '', userPhone: '', roleIds: [] }
  editorVisible.value = true
}

function openEdit(row: UserRow) {
  editorMode.value = 'edit'
  editor.value = {
    userId: row.id,
    userAccount: row.userAccount,
    userName: row.userName,
    userPhone: row.userPhone || '',
    roleIds: [...(row.roleIds || [])],
  }
  editorVisible.value = true
}

async function save() {
  if (editorMode.value === 'create' && !editor.value.userAccount) {
    ElMessage.warning('请填写账号')
    return
  }
  if (!editor.value.userName) {
    ElMessage.warning('请填写姓名')
    return
  }
  saving.value = true
  try {
    let path = '/vms/api/sysUser/create'
    const payload: Record<string, any> = {
      userName: editor.value.userName,
      userPhone: editor.value.userPhone,
      roleIds: (editor.value.roleIds || []).join(','),
    }
    if (editorMode.value === 'create') {
      payload.userAccount = editor.value.userAccount
    } else {
      path = `/vms/api/sysUser/${editor.value.userId}/update`
    }
    const res = await api.post<any>(path, payload, { form: true })
    handleResult(res, '保存成功')
    if (res?.code === 0 || res?.type === 'success') {
      editorVisible.value = false
      loadList()
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function resetPwd(row: UserRow) {
  try {
    await ElMessageBox.confirm(`确定重置 ${row.userName} 的密码？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>(`/vms/api/sysUser/${row.id}/resetPwd`, {}, { form: true })
    handleResult(res, '重置成功')
  } catch (e) {
    ElMessage.error('重置失败')
  }
}

async function deleteSelected() {
  if (!selectedRows.value.length) return
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个用户？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/sysUser/delete', {
      ids: selectedRows.value.map((r) => r.id).join(','),
    }, { form: true })
    handleResult(res, '删除成功')
    loadList()
  } catch (e) {
    ElMessage.error('删除失败')
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
  loadRoles()
  loadList()
})
</script>

<style scoped>
.user-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
