<template>
  <div class="app" :class="{ 'app-login': !userStore.isLogin }">
    <aside v-if="userStore.isLogin" class="sidebar">
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

        <el-sub-menu v-if="showGroup(gTask)" index="g-task">
          <template #title>工单管理</template>
          <el-menu-item v-if="show('/delivery-task')" index="/delivery-task">配送工单</el-menu-item>
          <el-menu-item v-if="show('/repair-task')" index="/repair-task">维护工单</el-menu-item>
          <el-menu-item v-if="show('/deploy-task')" index="/deploy-task">投放工单</el-menu-item>
          <el-menu-item v-if="show('/withdraw-task')" index="/withdraw-task">撤机工单</el-menu-item>
          <el-menu-item v-if="show('/task-search')" index="/task-search">工单查询</el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="showGroup(gVm)" index="g-vm">
          <template #title>售货机管理</template>
          <el-menu-item v-if="show('/vm')" index="/vm">售货机管理</el-menu-item>
          <el-menu-item v-if="show('/vm-channel')" index="/vm-channel">货道配置</el-menu-item>
          <el-menu-item v-if="show('/vm-price')" index="/vm-price">价格配置</el-menu-item>
          <el-menu-item v-if="show('/vm-energy')" index="/vm-energy">节能配置</el-menu-item>
          <el-menu-item v-if="show('/vm-shortcuts')" index="/vm-shortcuts">闪购键配置</el-menu-item>
          <el-menu-item v-if="show('/vm-text')" index="/vm-text">走马灯配置</el-menu-item>
          <el-menu-item v-if="show('/vm-theme')" index="/vm-theme">皮肤配置</el-menu-item>
          <el-menu-item v-if="show('/vm-stock')" index="/vm-stock">售货机库存</el-menu-item>
          <el-menu-item v-if="show('/vm-status')" index="/vm-status">售货机状态</el-menu-item>
          <el-menu-item v-if="show('/vm-status-his')" index="/vm-status-his">历史状态</el-menu-item>
          <el-menu-item v-if="show('/unique-code')" index="/unique-code">设备MAC</el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="showGroup(gBase)" index="g-base">
          <template #title>商品与基础数据</template>
          <el-menu-item v-if="show('/dictionary')" index="/dictionary">字典管理</el-menu-item>
          <el-menu-item v-if="show('/sku-type')" index="/sku-type">商品类型</el-menu-item>
          <el-menu-item v-if="show('/sku')" index="/sku">商品管理</el-menu-item>
          <el-menu-item v-if="show('/mt-node')" index="/mt-node">点位管理</el-menu-item>
          <el-menu-item v-if="show('/district')" index="/district">商圈信息</el-menu-item>
          <el-menu-item v-if="show('/warehouse')" index="/warehouse">仓库信息</el-menu-item>
          <el-menu-item v-if="show('/sim')" index="/sim">SIM卡信息</el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="showGroup(gStrategy)" index="g-strategy">
          <template #title>策略管理</template>
          <el-menu-item v-if="show('/channel-strategy')" index="/channel-strategy">货道策略</el-menu-item>
          <el-menu-item v-if="show('/price-strategy')" index="/price-strategy">价格策略</el-menu-item>
          <el-menu-item v-if="show('/shortcut-strategy')" index="/shortcut-strategy">闪购键策略</el-menu-item>
          <el-menu-item v-if="show('/text-strategy')" index="/text-strategy">走马灯策略</el-menu-item>
          <el-menu-item v-if="show('/energy-strategy')" index="/energy-strategy">节能策略</el-menu-item>
          <el-menu-item v-if="show('/theme-strategy')" index="/theme-strategy">皮肤策略</el-menu-item>
          <el-menu-item v-if="show('/sku-event-strategy')" index="/sku-event-strategy">商品促销策略</el-menu-item>
          <el-menu-item v-if="show('/vm-event-strategy')" index="/vm-event-strategy">售货机促销策略</el-menu-item>
          <el-menu-item v-if="show('/applied-event')" index="/applied-event">应用促销策略</el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="showGroup(gOrg)" index="g-org">
          <template #title>组织管理</template>
          <el-menu-item v-if="show('/company')" index="/company">公司管理</el-menu-item>
          <el-menu-item v-if="show('/customer')" index="/customer">客户信息</el-menu-item>
          <el-menu-item v-if="show('/partner')" index="/partner">合伙人信息</el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="showGroup(gSys)" index="g-sys">
          <template #title>系统管理</template>
          <el-menu-item v-if="show('/user')" index="/user">用户管理</el-menu-item>
          <el-menu-item v-if="show('/role')" index="/role">角色管理</el-menu-item>
          <el-menu-item v-if="show('/account')" index="/account">账号管理</el-menu-item>
          <el-menu-item v-if="show('/sys-log')" index="/sys-log">系统日志</el-menu-item>
          <el-menu-item v-if="show('/settings')" index="/settings">系统设置</el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="showGroup(gFinance)" index="g-finance">
          <template #title>财务</template>
          <el-menu-item v-if="show('/customer-account')" index="/customer-account">支付管理</el-menu-item>
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

/**
 * 菜单项 → 权限码（menu-vms.md 既有登记）。
 * 登录时后端装载权限缓存，/auth/me 返回真实 permissions；写操作另有接口级强校验，
 * 这里只控制菜单可见性——直连 URL 仍可进入只读页面，写操作由后端 403 兜底。
 * 未登记权限码的页面（如工作台）不设限。
 */
const MENU_PERMS: Record<string, string> = {
  '/delivery-task': 'delivery-task',
  '/repair-task': 'repair-task',
  '/deploy-task': 'deploy-task',
  '/withdraw-task': 'withdraw-task',
  '/task-search': 'search-task',
  '/vm': 'vm-manage',
  '/vm-channel': 'vm-set-channel',
  '/vm-price': 'vm-set-price',
  '/vm-energy': 'vm-set-energy',
  '/vm-shortcuts': 'vm-set-shortcuts',
  '/vm-text': 'vm-set-text',
  '/vm-theme': 'vm-set-theme',
  '/vm-stock': 'vmStock-list',
  '/vm-status': 'vmStatus-list',
  '/vm-status-his': 'vmStatusHis-list',
  '/unique-code': 'uniqueCode',
  '/dictionary': 'sysType-home',
  '/sku-type': 'skuType-home',
  '/sku': 'sku-home',
  '/mt-node': 'mtNode',
  '/district': 'businessDistrict-home',
  '/warehouse': 'warehouse-uniteArea',
  '/sim': 'sim-list',
  '/channel-strategy': 'st-vm-channel',
  '/price-strategy': 'stSkuPrice',
  '/shortcut-strategy': 'st-shortcuts',
  '/text-strategy': 'st-vm-text',
  '/energy-strategy': 'st-vm-energy',
  '/theme-strategy': 'st-theme-list',
  '/sku-event-strategy': 'stSkuEvent',
  '/vm-event-strategy': 'stVmEvent',
  '/applied-event': 'mtVmEvent',
  '/company': 'company_home',
  '/customer': 'customer-home',
  '/partner': 'partner-home',
  '/user': 'user-list',
  '/role': 'role-list',
  '/account': 'account-home',
  '/sys-log': 'sysLog-home',
  '/settings': 'sysSetting-list',
  '/customer-account': 'customerAccount-home',
}

const gTask = ['/delivery-task', '/repair-task', '/deploy-task', '/withdraw-task', '/task-search']
const gVm = ['/vm', '/vm-channel', '/vm-price', '/vm-energy', '/vm-shortcuts', '/vm-text', '/vm-theme', '/vm-stock', '/vm-status', '/vm-status-his', '/unique-code']
const gBase = ['/dictionary', '/sku-type', '/sku', '/mt-node', '/district', '/warehouse', '/sim']
const gStrategy = ['/channel-strategy', '/price-strategy', '/shortcut-strategy', '/text-strategy', '/energy-strategy', '/theme-strategy', '/sku-event-strategy', '/vm-event-strategy', '/applied-event']
const gOrg = ['/company', '/customer', '/partner']
const gSys = ['/user', '/role', '/account', '/sys-log', '/settings']
const gFinance = ['/customer-account']

function show(path: string): boolean {
  const code = MENU_PERMS[path]
  return !code || userStore.hasPermission(code)
}

function showGroup(paths: string[]): boolean {
  return paths.some((p) => show(p))
}
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
/* 未登录（登录页）时主区域全屏，无侧边栏 */
.app-login .content {
  padding: 0;
}
</style>
