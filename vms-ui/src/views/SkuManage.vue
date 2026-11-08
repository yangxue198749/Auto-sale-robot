<template>
  <div class="sku-manage">
    <h1>商品管理</h1>
    <p class="hint">管理商品信息（名称、编码、类型、价格、图片）。</p>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="showDialog()">新增商品</el-button>
    </div>

    <!-- 筛选 -->
    <el-form :inline="true" class="filter-form">
      <el-form-item label="名称">
        <el-input v-model="filters.name" placeholder="商品名称" clearable />
      </el-form-item>
      <el-form-item label="编码">
        <el-input v-model="filters.skuCode" placeholder="商品编码" clearable />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="filters.skuTypeId" placeholder="全部类型" clearable filterable style="width: 160px">
          <el-option v-for="t in skuTypes" :key="t.id" :label="t.typeName" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadSkus">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 列表 -->
    <el-table :data="skus" v-loading="loading" border stripe style="width: 100%">
      <el-table-column label="图片" width="90">
        <template #default="scope">
          <el-image :src="scope.row.imgUrl" fit="cover" style="width: 60px; height: 60px" />
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="skuName" label="商品名称" min-width="160" />
      <el-table-column prop="skuCode" label="编码" width="120" />
      <el-table-column prop="skuTypeName" label="类型" width="120" />
      <el-table-column prop="offerPrice" label="售价(分)" width="100" />
      <el-table-column prop="skuUnit" label="单位" width="80" />
      <el-table-column prop="skuProducer" label="生产商" min-width="140" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button type="primary" link @click="showDialog(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadSkus"
      @current-change="loadSkus"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '新增商品'" width="700px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商品名称" required>
              <el-input v-model="form.skuName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品编码">
              <el-input v-model="form.skuCode" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商品类型" required>
              <el-select v-model="form.skuTypeId" filterable placeholder="选择类型" style="width: 100%">
                <el-option v-for="t in skuTypes" :key="t.id" :label="t.typeName" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售价(分)">
              <el-input-number v-model="form.offerPrice" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="form.skuUnit" placeholder="如：瓶/罐" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产商">
              <el-input v-model="form.skuProducer" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="保质期(天)">
              <el-input-number v-model="form.skuShelfLife" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产地">
              <el-input v-model="form.skuMadeArea" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input v-model="form.skuDesc" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="商品主图">
          <div class="img-upload">
            <el-image v-if="form.imgUrl && form.id" :src="form.imgUrl" fit="cover" style="width: 80px; height: 80px; margin-right: 12px" />
            <el-upload
              :show-file-list="false"
              :disabled="!form.id"
              :http-request="uploadImage"
              accept=".png"
            >
              <el-button size="small" :disabled="!form.id">
                {{ form.id ? '上传主图' : '保存后上传' }}
              </el-button>
            </el-upload>
          </div>
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
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { api } from '../api/client'

interface Sku {
  id: number
  skuName: string
  skuCode: string
  skuTypeId: number
  skuTypeName: string
  offerPrice: number
  skuUnit: string
  skuDesc: string
  skuProducer: string
  skuShelfLife: number
  skuMadeArea: string
  imgUrl: string
}

interface SkuType {
  id: number
  typeName: string
}

const skus = ref<Sku[]>([])
const loading = ref(false)
const skuTypes = ref<SkuType[]>([])
const pagination = ref({ page: 1, pageSize: 10, total: 0 })

const filters = ref({
  name: '',
  skuCode: '',
  skuTypeId: undefined as number | undefined,
})

const dialogVisible = ref(false)
const form = ref({
  id: undefined as number | undefined,
  skuName: '',
  skuCode: '',
  skuTypeId: undefined as number | undefined,
  offerPrice: 0,
  skuUnit: '',
  skuDesc: '',
  skuProducer: '',
  skuShelfLife: 0,
  skuMadeArea: '',
  imgUrl: '',
})
const submitting = ref(false)

async function loadSkuTypes() {
  try {
    const res = await api.get<{ total: number; data: SkuType[] }>('/vms/api/skuType/list?page=1&pageSize=201')
    skuTypes.value = res.data
  } catch (e) {
    // ignore
  }
}

async function loadSkus() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.append('page', pagination.value.page.toString())
    params.append('pageSize', pagination.value.pageSize.toString())
    if (filters.value.name) params.append('name', filters.value.name)
    if (filters.value.skuCode) params.append('skuCode', filters.value.skuCode)
    if (filters.value.skuTypeId !== undefined) params.append('skuTypeId', filters.value.skuTypeId.toString())

    const res = await api.get<{ total: number; data: Sku[] }>(`/vms/api/sku/list?${params}`)
    skus.value = res.data
    pagination.value.total = res.total
  } catch (e: any) {
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  filters.value = { name: '', skuCode: '', skuTypeId: undefined }
  pagination.value.page = 1
  loadSkus()
}

function showDialog(row?: Sku) {
  if (row) {
    form.value = {
      id: row.id,
      skuName: row.skuName,
      skuCode: row.skuCode || '',
      skuTypeId: row.skuTypeId,
      offerPrice: row.offerPrice || 0,
      skuUnit: row.skuUnit || '',
      skuDesc: row.skuDesc || '',
      skuProducer: row.skuProducer || '',
      skuShelfLife: row.skuShelfLife || 0,
      skuMadeArea: row.skuMadeArea || '',
      imgUrl: row.imgUrl || '',
    }
  } else {
    form.value = {
      id: undefined,
      skuName: '',
      skuCode: '',
      skuTypeId: undefined,
      offerPrice: 0,
      skuUnit: '',
      skuDesc: '',
      skuProducer: '',
      skuShelfLife: 0,
      skuMadeArea: '',
      imgUrl: '',
    }
  }
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.value.skuName) {
    ElMessage.warning('请输入商品名称')
    return
  }
  if (form.value.skuTypeId === undefined) {
    ElMessage.warning('请选择商品类型')
    return
  }
  submitting.value = true
  try {
    const formData = new FormData()
    formData.append('skuName', form.value.skuName)
    formData.append('skuTypeId', form.value.skuTypeId.toString())
    if (form.value.skuCode) formData.append('skuCode', form.value.skuCode)
    if (form.value.offerPrice !== undefined) formData.append('offerPrice', form.value.offerPrice.toString())
    if (form.value.skuUnit) formData.append('skuUnit', form.value.skuUnit)
    if (form.value.skuDesc) formData.append('skuDesc', form.value.skuDesc)
    if (form.value.skuProducer) formData.append('skuProducer', form.value.skuProducer)
    if (form.value.skuShelfLife) formData.append('skuShelfLife', form.value.skuShelfLife.toString())
    if (form.value.skuMadeArea) formData.append('skuMadeArea', form.value.skuMadeArea)

    const url = form.value.id ? `/vms/api/sku/${form.value.id}/update` : '/vms/api/sku/create'
    const res = await api.post<{ success: boolean; message: string; id?: number }>(url, formData)
    if (res.success) {
      ElMessage.success(res.message)
      if (!form.value.id && res.id) {
        // 新建成功，转换为编辑模式以便上传图片
        form.value.id = res.id
        loadSkus()
      } else {
        dialogVisible.value = false
        loadSkus()
      }
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

async function uploadImage(options: UploadRequestOptions) {
  if (!form.value.id) return
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await api.post<{ success: boolean; message: string; imgUrl?: string }>(
      `/vms/api/sku/${form.value.id}/image`,
      formData,
    )
    if (res.success) {
      ElMessage.success(res.message)
      form.value.imgUrl = res.imgUrl ? `${window.location.origin}/vmsimgs/${res.imgUrl}` : form.value.imgUrl
      loadSkus()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error('上传失败')
  }
}

onMounted(() => {
  loadSkuTypes()
  loadSkus()
})
</script>

<style scoped>
.sku-manage h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.action-bar { margin-bottom: 16px; }
.filter-form { margin-bottom: 16px; padding: 16px; background: white; border-radius: 4px; }
.img-upload { display: flex; align-items: center; }
</style>
