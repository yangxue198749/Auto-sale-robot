<template>
  <div class="order-list">
    <h1>订单流水</h1>
    <p class="hint">按日期查询销售订单，可对未退款订单发起退款（走原支付渠道）。</p>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="yyyy-MM-dd" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
      </el-form-item>
      <el-form-item label="商品">
        <el-input v-model="filterSkuName" placeholder="商品名" clearable style="width: 140px" @keyup.enter="loadList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="loadList">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="rows" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="orderTime" label="下单时间" min-width="150" />
      <el-table-column prop="innerCode" label="设备" width="110" />
      <el-table-column prop="nodeName" label="点位" min-width="120" show-overflow-tooltip />
      <el-table-column prop="orderSeq" label="订单号" min-width="150" show-overflow-tooltip />
      <el-table-column prop="orderType" label="类型" width="80" />
      <el-table-column prop="skuName" label="商品" min-width="130" show-overflow-tooltip />
      <el-table-column prop="payType" label="支付" width="100" />
      <el-table-column prop="payAmount" label="支付金额" width="90" />
      <el-table-column prop="orderStatus" label="订单状态" width="100" />
      <el-table-column prop="refundStatus" label="退款状态" width="100" />
      <el-table-column label="操作" width="80" fixed="right">
        <template #default="scope">
          <el-button v-if="scope.row.refundButton" link type="warning" @click="refund(scope.row)">退款</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api/client'

interface OrderRow {
  detailId: string
  orderSeq: string
  orderTime: string
  innerCode: string
  nodeName: string
  orderType: string
  skuName: string
  payType: string
  payAmount: string
  orderStatus: string
  refundStatus: string
  refundButton: boolean
}

const rows = ref<OrderRow[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const filterSkuName = ref('')
const dateRange = ref<string[]>([])

async function loadList() {
  loading.value = true
  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('pageSize', String(pageSize.value))
    if (dateRange.value && dateRange.value.length === 2) {
      params.set('startDate', dateRange.value[0])
      params.set('endDate', dateRange.value[1])
    }
    if (filterSkuName.value) params.set('skuName', filterSkuName.value)
    const res = await api.get<{ total: number; data: OrderRow[] }>(`/vms/api/order/list?${params.toString()}`)
    rows.value = res.data
    total.value = res.total
  } catch (e) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(p: number) {
  page.value = p
  loadList()
}

async function refund(row: OrderRow) {
  try {
    await ElMessageBox.confirm(`确定为订单 ${row.orderSeq} 发起退款？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const res = await api.post<any>('/vms/api/order/refund', { detailId: row.detailId }, { form: true })
    if (res?.code === 0 || res?.type === 'success') {
      ElMessage.success('退款已发起')
      loadList()
    } else {
      ElMessage.error(res?.message || '退款失败')
    }
  } catch (e) {
    ElMessage.error('退款失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.order-list h1 { margin-bottom: 8px; }
.hint { color: #7f8c8d; font-size: 13px; margin-bottom: 16px; }
.filter-form { padding: 16px 16px 0; background: white; border-radius: 4px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
