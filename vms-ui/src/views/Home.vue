<template>
  <div class="home">
    <h1>工作台</h1>
    <p v-if="summary" class="date">今日指标（{{ summary.date }}）</p>

    <div v-if="error" class="error">
      <p>指标加载失败：{{ error }}</p>
      <button @click="load">重试</button>
    </div>

    <div v-else class="cards">
      <router-link v-if="summary" class="card task-card" to="/delivery-task">
        <div class="label">今日配送工单</div>
        <div class="value">{{ summary.delivery.ongoing }}</div>
        <div class="sub">未完成（含进行中）</div>
        <div class="sub muted">完成 {{ summary.delivery.finished }} · 关闭 {{ summary.delivery.closed }}</div>
      </router-link>
      <router-link v-if="summary" class="card task-card" to="/repair-task">
        <div class="label">今日维护工单</div>
        <div class="value">{{ summary.repair.ongoing }}</div>
        <div class="sub">未完成（含进行中）</div>
        <div class="sub muted">完成 {{ summary.repair.finished }} · 关闭 {{ summary.repair.closed }}</div>
      </router-link>
      <router-link v-if="summary" class="card task-card" to="/deploy-task">
        <div class="label">今日投放工单</div>
        <div class="value">{{ summary.deploy.ongoing }}</div>
        <div class="sub">未完成（含进行中）</div>
        <div class="sub muted">完成 {{ summary.deploy.finished }} · 关闭 {{ summary.deploy.closed }}</div>
      </router-link>
      <router-link v-if="summary" class="card task-card" to="/withdraw-task">
        <div class="label">今日撤机工单</div>
        <div class="value">{{ summary.withdraw.ongoing }}</div>
        <div class="sub">未完成（含进行中）</div>
        <div class="sub muted">完成 {{ summary.withdraw.finished }} · 关闭 {{ summary.withdraw.closed }}</div>
      </router-link>
      <router-link v-if="summary" class="card" to="/mt-node">
        <div class="label">今日新增点位</div>
        <div class="value">{{ summary.newNodeCount }}</div>
      </router-link>
      <router-link v-if="summary" class="card" to="/vm">
        <div class="label">今日新增设备</div>
        <div class="value">{{ summary.newVmCount }}</div>
      </router-link>
      <router-link v-if="summary" class="card" to="/applied-event">
        <div class="label">今日在促销设备</div>
        <div class="value">{{ summary.eventVmCount }}</div>
      </router-link>
      <router-link v-if="summary" class="card" to="/vm-stock">
        <div class="label">今日缺货设备</div>
        <div class="value">{{ summary.oosVmCount }}</div>
      </router-link>
      <router-link v-if="summary" class="card" to="/order">
        <div class="label">今日销量</div>
        <div class="value">{{ summary.salesVolume }}</div>
      </router-link>
      <router-link v-if="summary" class="card" to="/order">
        <div class="label">今日销售额</div>
        <div class="value">{{ summary.salesAmount }}</div>
      </router-link>
    </div>

    <p v-if="!summary && !error" class="loading">加载中…</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getWorkbenchSummary, type WorkbenchSummary } from '../api/workbench'

const summary = ref<WorkbenchSummary | null>(null)
const error = ref('')

async function load() {
  error.value = ''
  summary.value = null
  try {
    summary.value = await getWorkbenchSummary()
  } catch (e: any) {
    error.value = e?.message || '网络异常'
  }
}

onMounted(load)
</script>

<style scoped>
.home h1 {
  margin-bottom: 8px;
}
.date {
  color: #7f8c8d;
  font-size: 13px;
  margin-bottom: 8px;
}
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 16px;
}
.card {
  display: block;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  text-decoration: none;
  color: inherit;
  transition: box-shadow 0.15s;
}
.card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.18);
}
.card .label {
  font-size: 13px;
  color: #7f8c8d;
  margin-bottom: 8px;
}
.card .value {
  font-size: 26px;
  font-weight: 600;
  color: #2c3e50;
}
.card .sub {
  font-size: 12px;
  color: #95a5a6;
  margin-top: 6px;
}
.card .sub.muted {
  color: #b2bec3;
}
.error {
  margin-top: 24px;
  color: #c0392b;
}
.error button {
  margin-top: 8px;
  padding: 4px 16px;
}
.loading {
  margin-top: 24px;
  color: #7f8c8d;
}
</style>
