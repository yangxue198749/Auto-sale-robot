<template>
  <div class="app">
    <aside class="sidebar">
      <h2>售货机管理系统</h2>
      <nav>
        <RouterLink to="/">工作台</RouterLink>
        <RouterLink to="/delivery-task">配送工单</RouterLink>
        <RouterLink to="/repair-task">维护工单</RouterLink>
        <RouterLink to="/deploy-task">投放工单</RouterLink>
        <RouterLink to="/withdraw-task">撤机工单</RouterLink>
        <RouterLink to="/task-search">工单查询</RouterLink>
        <RouterLink to="/dictionary">字典管理</RouterLink>
        <RouterLink to="/sku-type">商品类型</RouterLink>
        <RouterLink to="/mt-node">点位管理</RouterLink>
        <RouterLink to="/sku">商品管理</RouterLink>
        <RouterLink to="/vm-model">型号货道</RouterLink>
        <RouterLink to="/vm">售货机管理</RouterLink>
        <RouterLink to="/vm-channel">货道配置</RouterLink>
        <RouterLink to="/vm-price">价格配置</RouterLink>
        <RouterLink to="/vm-energy">节能配置</RouterLink>
        <RouterLink to="/vm-shortcuts">闪购键配置</RouterLink>
        <RouterLink to="/vm-text">走马灯配置</RouterLink>
        <RouterLink to="/vm-theme">皮肤配置</RouterLink>
        <RouterLink to="/channel-strategy">货道策略</RouterLink>
        <RouterLink to="/shortcut-strategy">闪购键策略</RouterLink>
        <RouterLink to="/text-strategy">走马灯策略</RouterLink>
        <RouterLink to="/energy-strategy">节能策略</RouterLink>
        <RouterLink to="/theme-strategy">皮肤策略</RouterLink>
        <RouterLink to="/about">关于</RouterLink>
      </nav>
      <div class="user">
        <span v-if="userStore.isLogin">用户: {{ userStore.userName }}</span>
        <span v-else>未登录</span>
      </div>
    </aside>
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useUserStore } from './stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.app {
  display: flex;
  min-height: 100vh;
}
.sidebar {
  width: 200px;
  background: #2c3e50;
  color: white;
  padding: 20px;
}
.sidebar h2 {
  font-size: 16px;
  margin-bottom: 20px;
}
.sidebar nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.sidebar a {
  color: #ecf0f1;
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 4px;
}
.sidebar a:hover {
  background: #34495e;
}
.sidebar a.router-link-active {
  background: #3498db;
}
.user {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #34495e;
  font-size: 12px;
  color: #bdc3c7;
}
.content {
  flex: 1;
  padding: 20px;
  background: #ecf0f1;
}
</style>
