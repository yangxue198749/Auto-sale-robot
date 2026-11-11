<template>
  <div class="warehouse-view">
    <h1>仓库信息</h1>
    <p class="hint">查看仓库档案与已绑定配送区域（绑定区域依赖省市区数据源）。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="筛选">
        <el-select v-model="filterType" style="width: 110px">
          <el-option label="仓库编码" value="1" />
          <el-option label="仓库名称" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="filterText" placeholder="关键字" clearable style="width: 150px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="warehouseCode" label="编码" width="110" />
      <el-table-column prop="warehouseName" label="名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="warehouseAddress" label="地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="warehouseManager" label="负责人" width="100" />
      <el-table-column prop="warehousePhone" label="电话" width="130" />
      <el-table-column prop="areaCount" label="绑定区域" width="90" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openAreas(scope.row)">查看区域</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="onPageChange" />

    <el-dialog v-model="areasVisible" :title="`配送区域：${currentWarehouse?.warehouseName || ''}`" width="480px">
      <el-table :data="areas" size="small" border style="width: 100%">
        <el-table-column prop="province" label="省" min-width="110" />
        <el-table-column prop="city" label="市" min-width="110" />
        <el-table-column prop="area" label="区" min-width="110" />
      </el-table>
      <div v-if="!areas.length" class="empty-tip">未绑定区域</div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api/client'

interface WarehouseRow {
  id: number
  warehouseCode: string | null
  warehouseName: string | null
  warehouseAddress: string | null
  warehouseManager: string | null
  warehousePhone: string | null
  areaCount: number
}

interface AreaRow {
  province: string
  city: string
  area: string
}

const rows = ref<WarehouseRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterType = ref('1')
const filterText = ref('')

const areasVisible = ref(false)
const currentWarehouse = ref<WarehouseRow | null>(null)
const areas = ref<AreaRow[]>([])

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (filterText.value) {
      params.set('searchType', filterType.value)
      params.set('searchValue', filterText.value)
    }
    const res = await api.get<{ total: number; data: WarehouseRow[] }>(`/vms/api/warehouse/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载仓库失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

async function openAreas(row: WarehouseRow) {
  currentWarehouse.value = row
  areasVisible.value = true
  try {
    areas.value = await api.get<AreaRow[]>(`/vms/api/warehouse/${row.id}/areas`)
  } catch (e) {
    ElMessage.error('加载区域失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.warehouse-view h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.empty-tip { color: #95a5a6; text-align: center; padding: 20px 0; }
</style>
