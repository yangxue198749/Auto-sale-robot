<template>
  <div class="app">
    <aside class="sidebar">
      <h2>售货机管理系统</h2>
      <el-menu
        :default-active="activePath"
        router
        class="side-menu"
        background-color="#2c3e50"
        text-color="#ecf0f1"
        active-text-color="#ffffff"
        unique-opened
      >
        <el-menu-item index="/">
          <span>工作台</span>
        </el-menu-item>

        <el-sub-menu index="g-task">
          <template #title>工单管理</template>
          <el-menu-item index="/delivery-task">配送工单</el-menu-item>
          <el-menu-item index="/repair-task">维护工单</el-menu-item>
          <el-menu-item index="/deploy-task">投放工单</el-menu-item>
          <el-menu-item index="/withdraw-task">撤机工单</el-menu-item>
          <el-menu-item index="/task-search">工单查询</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="g-vm">
          <template #title>售货机管理</template>
          <el-menu-item index="/vm">售货机管理</el-menu-item>
          <el-menu-item index="/vm-channel">货道配置</el-menu-item>
          <el-menu-item index="/vm-price">价格配置</el-menu-item>
          <el-menu-item index="/vm-energy">节能配置</el-menu-item>
          <el-menu-item index="/vm-shortcuts">闪购键配置</el-menu-item>
          <el-menu-item index="/vm-text">走马灯配置</el-menu-item>
          <el-menu-item index="/vm-theme">皮肤配置</el-menu-item>
          <el-menu-item index="/vm-stock">售货机库存</el-menu-item>
          <el-menu-item index="/vm-status">售货机状态</el-menu-item>
          <el-menu-item index="/vm-status-his">历史状态</el-menu-item>
          <el-menu-item index="/unique-code">设备MAC</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="g-base">
          <template #title>商品与基础数据</template>
          <el-menu-item index="/dictionary">字典管理</el-menu-item>
          <el-menu-item index="/sku-type">商品类型</el-menu-item>
          <el-menu-item index="/sku">商品管理</el-menu-item>
          <el-menu-item index="/mt-node">点位管理</el-menu-item>
          <el-menu-item index="/district">商圈信息</el-menu-item>
          <el-menu-item index="/warehouse">仓库信息</el-menu-item>
          <el-menu-item index="/sim">SIM卡信息</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="g-strategy">
          <template #title>策略管理</template>
          <el-menu-item index="/channel-strategy">货道策略</el-menu-item>
          <el-menu-item index="/price-strategy">价格策略</el-menu-item>
          <el-menu-item index="/shortcut-strategy">闪购键策略</el-menu-item>
          <el-menu-item index="/text-strategy">走马灯策略</el-menu-item>
          <el-menu-item index="/energy-strategy">节能策略</el-menu-item>
          <el-menu-item index="/theme-strategy">皮肤策略</el-menu-item>
          <el-menu-item index="/sku-event-strategy">商品促销策略</el-menu-item>
          <el-menu-item index="/vm-event-strategy">售货机促销策略</el-menu-item>
          <el-menu-item index="/applied-event">应用促销策略</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="g-org">
          <template #title>组织管理</template>
          <el-menu-item index="/company">公司管理</el-menu-item>
          <el-menu-item index="/customer">客户信息</el-menu-item>
          <el-menu-item index="/partner">合伙人信息</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="g-sys">
          <template #title>系统管理</template>
          <el-menu-item index="/user">用户管理</el-menu-item>
          <el-menu-item index="/role">角色管理</el-menu-item>
          <el-menu-item index="/account">账号管理</el-menu-item>
          <el-menu-item index="/sys-log">系统日志</el-menu-item>
          <el-menu-item index="/settings">系统设置</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="g-finance">
          <template #title>财务</template>
          <el-menu-item index="/customer-account">支付管理</el-menu-item>
        </el-sub-menu>
      </el-menu>

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
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { useUserStore } from './stores/user'

const route = useRoute()
const userStore = useUserStore()
const activePath = computed(() => route.path)
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
  padding: 20px 0;
  display: flex;
  flex-direction: column;
}
.sidebar h2 {
  font-size: 16px;
  margin: 0 20px 16px;
  color: white;
}
.side-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}
.side-menu :deep(.el-menu-item) {
  height: 40px;
  line-height: 40px;
}
.side-menu :deep(.el-menu-item.is-active) {
  background: #3498db;
}
.side-menu :deep(.el-sub-menu__title) {
  height: 42px;
  line-height: 42px;
}
.side-menu :deep(.el-sub-menu .el-menu-item) {
  background: #34495e;
  min-width: 0;
}
.side-menu :deep(.el-sub-menu .el-menu-item:hover) {
  background: #3d566e;
}
.user {
  padding: 16px 20px 0;
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
