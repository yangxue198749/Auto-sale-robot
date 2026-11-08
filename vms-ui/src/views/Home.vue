<template>
  <div class="home">
    <h1>工作台</h1>
    <p>欢迎来到 VMS 售货机管理系统（Vue 3 前端）</p>

    <div class="cards">
      <div class="card">
        <div class="label">今日配送工单</div>
        <div class="value">{{ stats.deliveryCount }}</div>
      </div>
      <div class="card">
        <div class="label">设备数</div>
        <div class="value">{{ stats.vmCount }}</div>
      </div>
      <div class="card">
        <div class="label">数据接口</div>
        <div class="value">{{ stats.source }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const stats = ref({
  deliveryCount: 0,
  vmCount: 0,
  source: '未加载',
})

onMounted(() => {
  // 一期先展示静态数据，后续从后端 /workbench 接口获取
  stats.value = {
    deliveryCount: 1,
    vmCount: 1,
    source: 'Vue 3 + Vite',
  }
})
</script>

<style scoped>
.home h1 {
  margin-bottom: 16px;
}
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-top: 24px;
}
.card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}
.card .label {
  font-size: 13px;
  color: #7f8c8d;
  margin-bottom: 8px;
}
.card .value {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}
</style>
