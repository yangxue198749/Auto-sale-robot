<template>
  <div class="dictionary-manage">
    <h1>字典管理</h1>
    <p class="hint">管理字典类型及其字典项。左侧选择字典类型，右侧管理该类型下的字典项。</p>

    <div class="layout">
      <!-- 左侧：字典类型 -->
      <div class="panel types-panel">
        <div class="panel-header">
          <span>字典类型</span>
          <el-button type="primary" size="small" @click="showTypeDialog()">新增类型</el-button>
        </div>
        <el-input
          v-model="typeSearch"
          placeholder="搜索类型名称"
          size="small"
          clearable
          style="margin-bottom: 10px"
          @input="loadTypes"
        />
        <el-table
          :data="types"
          v-loading="typesLoading"
          highlight-current-row
          size="small"
          @current-change="onTypeSelect"
          style="width: 100%"
        >
          <el-table-column prop="typeName" label="类型名称" min-width="120" />
          <el-table-column prop="typeCode" label="编码" width="120" />
          <el-table-column label="操作" width="110">
            <template #default="scope">
              <el-button type="primary" link size="small" @click.stop="showTypeDialog(scope.row)">编辑</el-button>
              <el-button type="danger" link size="small" @click.stop="deleteType(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="typePagination.page"
          v-model:page-size="typePagination.pageSize"
          :total="typePagination.total"
          layout="total, prev, pager, next"
          small
          @current-change="loadTypes"
          style="margin-top: 10px"
        />
      </div>

      <!-- 右侧：字典项 -->
      <div class="panel items-panel">
        <div class="panel-header">
          <span>
            字典项
            <template v-if="selectedType"> — {{ selectedType.typeName }}</template>
          </span>
          <el-button type="primary" size="small" :disabled="!selectedType" @click="showItemDialog()">
            新增字典项
          </el-button>
        </div>
        <div v-if="!selectedType" class="empty-tip">请先在左侧选择一个字典类型</div>
        <template v-else>
          <el-table :data="items" v-loading="itemsLoading" size="small" style="width: 100%">
            <el-table-column prop="dictionaryName" label="名称" min-width="120" />
            <el-table-column prop="dictionaryCode" label="编码" min-width="100" />
            <el-table-column prop="dictionaryDesc" label="描述" min-width="120" show-overflow-tooltip />
            <el-table-column prop="dictionaryState" label="状态" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.dictionaryState === 0 ? 'success' : 'info'" size="small">
                  {{ scope.row.dictionaryState === 0 ? '启用' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="110">
              <template #default="scope">
                <el-button type="primary" link size="small" @click="showItemDialog(scope.row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="deleteItem(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="itemPagination.page"
            v-model:page-size="itemPagination.pageSize"
            :total="itemPagination.total"
            layout="total, prev, pager, next"
            small
            @current-change="loadItems"
            style="margin-top: 10px"
          />
        </template>
      </div>
    </div>

    <!-- 类型编辑对话框 -->
    <el-dialog v-model="typeDialogVisible" :title="typeForm.id ? '编辑字典类型' : '新增字典类型'" width="450px">
      <el-form :model="typeForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="typeForm.typeName" placeholder="如：订单状态" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="typeForm.typeCode" placeholder="如：order_status" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="typeForm.typeDesc" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="typeSubmitting" @click="submitTypeForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 字典项编辑对话框 -->
    <el-dialog v-model="itemDialogVisible" :title="itemForm.id ? '编辑字典项' : '新增字典项'" width="450px">
      <el-form :model="itemForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="itemForm.dictionaryName" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="itemForm.dictionaryCode" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="itemForm.dictionaryDesc" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="itemForm.dictionaryState">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="itemSubmitting" @click="submitItemForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface DictType {
  id: number
  typeCode: string
  typeName: string
  typeDesc: string
}

interface DictItem {
  id: number
  dictionaryCode: string
  dictionaryName: string
  dictionaryDesc: string
  dictionaryState: number
  typeInfoId: number
  typeInfoName: string
}

const types = ref<DictType[]>([])
const typesLoading = ref(false)
const typeSearch = ref('')
const typePagination = ref({ page: 1, pageSize: 20, total: 0 })

const items = ref<DictItem[]>([])
const itemsLoading = ref(false)
const itemPagination = ref({ page: 1, pageSize: 20, total: 0 })
const selectedType = ref<DictType | null>(null)

const typeDialogVisible = ref(false)
const typeForm = ref({ id: undefined as number | undefined, typeCode: '', typeName: '', typeDesc: '' })
const typeSubmitting = ref(false)

const itemDialogVisible = ref(false)
const itemForm = ref({
  id: undefined as number | undefined,
  dictionaryCode: '',
  dictionaryName: '',
  dictionaryDesc: '',
  dictionaryState: 0,
})
const itemSubmitting = ref(false)

async function loadTypes() {
  typesLoading.value = true
  try {
    const params = new URLSearchParams()
    params.append('page', typePagination.value.page.toString())
    params.append('pageSize', typePagination.value.pageSize.toString())
    if (typeSearch.value) params.append('name', typeSearch.value)
    const res = await api.get<{ total: number; data: DictType[] }>(`/vms/api/sysType/list?${params}`)
    types.value = res.data
    typePagination.value.total = res.total
  } catch (e: any) {
    ElMessage.error('加载字典类型失败')
  } finally {
    typesLoading.value = false
  }
}

function onTypeSelect(row: DictType | null) {
  selectedType.value = row
  itemPagination.value.page = 1
  if (row) loadItems()
}

async function loadItems() {
  if (!selectedType.value) return
  itemsLoading.value = true
  try {
    const params = new URLSearchParams()
    params.append('typeInfoId', selectedType.value.id.toString())
    params.append('page', itemPagination.value.page.toString())
    params.append('pageSize', itemPagination.value.pageSize.toString())
    const res = await api.get<{ total: number; data: DictItem[] }>(`/vms/api/dictionary/list?${params}`)
    items.value = res.data
    itemPagination.value.total = res.total
  } catch (e: any) {
    ElMessage.error('加载字典项失败')
  } finally {
    itemsLoading.value = false
  }
}

function showTypeDialog(row?: DictType) {
  if (row) {
    typeForm.value = { id: row.id, typeCode: row.typeCode, typeName: row.typeName, typeDesc: row.typeDesc }
  } else {
    typeForm.value = { id: undefined, typeCode: '', typeName: '', typeDesc: '' }
  }
  typeDialogVisible.value = true
}

async function submitTypeForm() {
  if (!typeForm.value.typeName) {
    ElMessage.warning('请输入类型名称')
    return
  }
  typeSubmitting.value = true
  try {
    const formData = new FormData()
    formData.append('typeName', typeForm.value.typeName)
    if (typeForm.value.typeCode) formData.append('typeCode', typeForm.value.typeCode)
    if (typeForm.value.typeDesc) formData.append('typeDesc', typeForm.value.typeDesc)

    const url = typeForm.value.id
      ? `/vms/api/sysType/${typeForm.value.id}/update`
      : '/vms/api/sysType/create'
    const res = await api.post<{ success: boolean; message: string }>(url, formData)
    if (res.success) {
      ElMessage.success(res.message)
      typeDialogVisible.value = false
      loadTypes()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    typeSubmitting.value = false
  }
}

async function deleteType(row: DictType) {
  try {
    await ElMessageBox.confirm(`确定删除字典类型「${row.typeName}」吗？`, '确认删除', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<{ success: boolean; message: string }>(
      `/vms/api/sysType/${row.id}/delete`,
      new FormData(),
    )
    if (res.success) {
      ElMessage.success(res.message)
      if (selectedType.value?.id === row.id) {
        selectedType.value = null
        items.value = []
      }
      loadTypes()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('删除失败')
  }
}

function showItemDialog(row?: DictItem) {
  if (row) {
    itemForm.value = {
      id: row.id,
      dictionaryCode: row.dictionaryCode,
      dictionaryName: row.dictionaryName,
      dictionaryDesc: row.dictionaryDesc,
      dictionaryState: row.dictionaryState,
    }
  } else {
    itemForm.value = { id: undefined, dictionaryCode: '', dictionaryName: '', dictionaryDesc: '', dictionaryState: 0 }
  }
  itemDialogVisible.value = true
}

async function submitItemForm() {
  if (!itemForm.value.dictionaryName) {
    ElMessage.warning('请输入字典项名称')
    return
  }
  if (!selectedType.value) {
    ElMessage.warning('请先选择字典类型')
    return
  }
  itemSubmitting.value = true
  try {
    const formData = new FormData()
    formData.append('dictionaryName', itemForm.value.dictionaryName)
    if (itemForm.value.dictionaryCode) formData.append('dictionaryCode', itemForm.value.dictionaryCode)
    if (itemForm.value.dictionaryDesc) formData.append('dictionaryDesc', itemForm.value.dictionaryDesc)
    formData.append('dictionaryState', itemForm.value.dictionaryState.toString())

    let url: string
    if (itemForm.value.id) {
      url = `/vms/api/dictionary/${itemForm.value.id}/update`
    } else {
      url = '/vms/api/dictionary/create'
      formData.append('typeInfoId', selectedType.value.id.toString())
    }
    const res = await api.post<{ success: boolean; message: string }>(url, formData)
    if (res.success) {
      ElMessage.success(res.message)
      itemDialogVisible.value = false
      loadItems()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    itemSubmitting.value = false
  }
}

async function deleteItem(row: DictItem) {
  try {
    await ElMessageBox.confirm(`确定删除字典项「${row.dictionaryName}」吗？`, '确认删除', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<{ success: boolean; message: string }>(
      `/vms/api/dictionary/${row.id}/delete`,
      new FormData(),
    )
    if (res.success) {
      ElMessage.success(res.message)
      loadItems()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => loadTypes())
</script>

<style scoped>
.dictionary-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.layout { display: flex; gap: 16px; align-items: flex-start; }
.panel { background: white; border-radius: 4px; padding: 16px; flex: 1; min-width: 0; }
.types-panel { max-width: 480px; }
.panel-header {
  display: flex; justify-content: space-between; align-items: center;
  font-weight: 600; margin-bottom: 12px;
}
.empty-tip { color: #95a5a6; text-align: center; padding: 40px 0; }
</style>
