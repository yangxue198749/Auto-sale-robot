<template>
  <div class="role-manage">
    <h1>角色管理</h1>
    <p class="hint">维护公司角色与权限元素绑定。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="角色名称">
        <el-input v-model="filterName" placeholder="角色名称" clearable style="width: 180px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="openCreate">新建角色</el-button>
        <el-button type="danger" :disabled="!selectedRows.length" @click="deleteSelected">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column prop="roleName" label="角色名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="roleDesc" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="roleTypeName" label="类型" width="90" />
      <el-table-column prop="userCount" label="用户数" width="80" />
      <el-table-column label="创建时间" min-width="160">
        <template #default="scope">{{ formatTime(scope.row.ctime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button link type="primary" @click="openElements(scope.row)">权限</el-button>
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

    <el-dialog v-model="editorVisible" :title="editorMode === 'create' ? '新建角色' : '编辑角色'" width="480px">
      <el-form :model="editor" label-width="90px">
        <el-form-item label="角色名称" required>
          <el-input v-model="editor.roleName" maxlength="60" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editor.roleDesc" type="textarea" :rows="2" maxlength="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="elementsVisible" :title="`权限配置：${currentRole?.roleName || ''}`" width="520px">
      <div v-loading="elementsLoading">
        <el-tree
          ref="treeRef"
          :data="elementTree"
          node-key="id"
          show-checkbox
          default-expand-all
          :props="{ label: 'name', children: 'children' }"
        />
      </div>
      <template #footer>
        <el-button @click="elementsVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingElements" @click="saveElements">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ElTree } from 'element-plus'
import { api } from '../api/client'

interface RoleRow {
  id: number
  roleName: string
  roleDesc: string
  roleType: number
  roleTypeName: string
  userCount: number
  ctime: string | null
}

interface ElementNode {
  id: number
  pid: number
  name: string
  children?: ElementNode[]
}

const rows = ref<RoleRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterName = ref('')
const selectedRows = ref<RoleRow[]>([])

const editorVisible = ref(false)
const editorMode = ref<'create' | 'edit'>('create')
const saving = ref(false)
const editor = ref<{ roleId?: number; roleName: string; roleDesc: string }>({ roleName: '', roleDesc: '' })

const elementsVisible = ref(false)
const elementsLoading = ref(false)
const savingElements = ref(false)
const currentRole = ref<RoleRow | null>(null)
const elementTree = ref<ElementNode[]>([])
const treeRef = ref<InstanceType<typeof ElTree>>()

function formatTime(value: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterName.value) params.set('roleName', filterName.value)
    const res = await api.get<{ total: number; data: RoleRow[] }>(`/vms/api/sysRole/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

function onSelectionChange(selection: RoleRow[]) {
  selectedRows.value = selection
}

function openCreate() {
  editorMode.value = 'create'
  editor.value = { roleName: '', roleDesc: '' }
  editorVisible.value = true
}

function openEdit(row: RoleRow) {
  editorMode.value = 'edit'
  editor.value = { roleId: row.id, roleName: row.roleName, roleDesc: row.roleDesc }
  editorVisible.value = true
}

async function save() {
  if (!editor.value.roleName) {
    ElMessage.warning('请填写角色名称')
    return
  }
  saving.value = true
  try {
    const payload: Record<string, any> = {
      roleName: editor.value.roleName,
      roleDesc: editor.value.roleDesc,
    }
    let path = '/vms/api/sysRole/create'
    if (editorMode.value === 'edit') {
      payload.roleId = editor.value.roleId
      path = '/vms/api/sysRole/edit'
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

async function openElements(row: RoleRow) {
  currentRole.value = row
  elementsVisible.value = true
  elementsLoading.value = true
  try {
    const res = await api.get<{ elements: ElementNode[]; checked: number[] }>(`/vms/api/sysRole/${row.id}/elements`)
    elementTree.value = buildTree(res.elements || [])
    await new Promise((r) => setTimeout(r, 100))
    treeRef.value?.setCheckedKeys(res.checked || [], false)
  } catch (e) {
    ElMessage.error('加载权限失败')
  } finally {
    elementsLoading.value = false
  }
}

function buildTree(list: ElementNode[]): ElementNode[] {
  const map = new Map<number, ElementNode>()
  for (const item of list) {
    map.set(item.id, { ...item, children: [] })
  }
  const roots: ElementNode[] = []
  for (const node of map.values()) {
    if (node.pid != null && map.has(node.pid)) {
      map.get(node.pid)!.children!.push(node)
    } else {
      roots.push(node)
    }
  }
  // 只勾选叶子节点，避免父节点联动覆盖
  for (const node of map.values()) {
    if (node.children && node.children.length === 0) delete node.children
  }
  return roots
}

async function saveElements() {
  if (!currentRole.value || !treeRef.value) return
  savingElements.value = true
  try {
    const checked: number[] = treeRef.value.getCheckedKeys(false) as number[]
    const half: number[] = treeRef.value.getHalfCheckedKeys() as number[]
    const ids = [...checked, ...half]
    const res = await api.post<any>('/vms/api/sysRole/bindElements', {
      roleId: currentRole.value.id,
      elementIds: ids.join(','),
    }, { form: true })
    handleResult(res, '保存成功')
    if (res?.code === 0 || res?.type === 'success') elementsVisible.value = false
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    savingElements.value = false
  }
}

async function deleteSelected() {
  if (!selectedRows.value.length) return
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个角色？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/sysRole/delete', {
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

onMounted(loadList)
</script>

<style scoped>
.role-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
