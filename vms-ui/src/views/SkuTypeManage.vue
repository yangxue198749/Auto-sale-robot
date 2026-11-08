<template>
  <div class="sku-type-manage">
    <h1>商品类型</h1>
    <p class="hint">管理商品类型（支持父子层级）。</p>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="showDialog()">新增类型</el-button>
    </div>

    <!-- 类型列表 -->
    <el-table :data="types" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="typeName" label="类型名称" min-width="200" />
      <el-table-column prop="externalId" label="外部编码" min-width="150" />
      <el-table-column label="父级" width="200">
        <template #default="scope">
          {{ parentName(scope.row.pId) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="showDialog(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="deleteType(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      layout="total, prev, pager, next"
      @current-change="loadTypes"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品类型' : '新增商品类型'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型名称">
          <el-input v-model="form.typeName" placeholder="如：饮料" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface SkuType {
  id: number
  typeName: string
  externalId: string
  pId: number | null
}

const types = ref<SkuType[]>([])
const loading = ref(false)
const pagination = ref({ page: 1, pageSize: 50, total: 0 })

const dialogVisible = ref(false)
const form = ref({ id: undefined as number | undefined, typeName: '' })
const submitting = ref(false)

function parentName(pId: number | null) {
  if (!pId) return '-'
  const parent = types.value.find((t) => t.id === pId)
  return parent ? parent.typeName : `ID:${pId}`
}

async function loadTypes() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.append('page', pagination.value.page.toString())
    params.append('pageSize', pagination.value.pageSize.toString())
    // 不传 pid 查询全部
    const res = await api.get<{ total: number; data: SkuType[] }>(`/vms/api/skuType/list?${params}`)
    types.value = res.data
    pagination.value.total = res.total
  } catch (e: any) {
    ElMessage.error('加载商品类型失败')
  } finally {
    loading.value = false
  }
}

function showDialog(row?: SkuType) {
  if (row) {
    form.value = { id: row.id, typeName: row.typeName }
  } else {
    form.value = { id: undefined, typeName: '' }
  }
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.value.typeName) {
    ElMessage.warning('请输入类型名称')
    return
  }
  submitting.value = true
  try {
    const formData = new FormData()
    formData.append('typeName', form.value.typeName)

    const url = form.value.id
      ? `/vms/api/skuType/${form.value.id}/update`
      : '/vms/api/skuType/create'
    const res = await api.post<{ success: boolean; message: string }>(url, formData)
    if (res.success) {
      ElMessage.success(res.message)
      dialogVisible.value = false
      loadTypes()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

async function deleteType(row: SkuType) {
  try {
    await ElMessageBox.confirm(`确定删除商品类型「${row.typeName}」吗？`, '确认删除', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<{ success: boolean; message: string }>(
      `/vms/api/skuType/${row.id}/delete`,
      new FormData(),
    )
    if (res.success) {
      ElMessage.success(res.message)
      loadTypes()
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
.sku-type-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.action-bar { margin-bottom: 16px; }
</style>
