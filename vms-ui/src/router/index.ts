import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  // hash 模式：静态部署到 Tomcat /dist/ 时无需服务端 fallback
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/delivery-task',
      name: 'deliveryTask',
      component: () => import('../views/DeliveryTask.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/repair-task',
      name: 'repairTask',
      component: () => import('../views/RepairTask.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/deploy-task',
      name: 'deployTask',
      component: () => import('../views/DeployTask.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/withdraw-task',
      name: 'withdrawTask',
      component: () => import('../views/WithdrawTask.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/task-search',
      name: 'taskSearch',
      component: () => import('../views/TaskSearch.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/dictionary',
      name: 'dictionary',
      component: () => import('../views/DictionaryManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/sku-type',
      name: 'skuType',
      component: () => import('../views/SkuTypeManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/mt-node',
      name: 'mtNode',
      component: () => import('../views/MtNodeManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/sku',
      name: 'sku',
      component: () => import('../views/SkuManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-model',
      name: 'vmModel',
      component: () => import('../views/VmModelManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm',
      name: 'vm',
      component: () => import('../views/VmManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-channel',
      name: 'vmChannel',
      component: () => import('../views/VmChannelConfig.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-price',
      name: 'vmPrice',
      component: () => import('../views/VmPriceConfig.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-energy',
      name: 'vmEnergy',
      component: () => import('../views/VmEnergyConfig.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-shortcuts',
      name: 'vmShortcuts',
      component: () => import('../views/VmShortcutsConfig.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-text',
      name: 'vmText',
      component: () => import('../views/VmTextConfig.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-theme',
      name: 'vmTheme',
      component: () => import('../views/VmThemeConfig.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/channel-strategy',
      name: 'channelStrategy',
      component: () => import('../views/VmChannelStrategy.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/shortcut-strategy',
      name: 'shortcutStrategy',
      component: () => import('../views/ShortcutStrategy.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/text-strategy',
      name: 'textStrategy',
      component: () => import('../views/TextStrategy.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/energy-strategy',
      name: 'energyStrategy',
      component: () => import('../views/EnergyStrategy.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/theme-strategy',
      name: 'themeStrategy',
      component: () => import('../views/ThemeStrategy.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/price-strategy',
      name: 'priceStrategy',
      component: () => import('../views/PriceStrategy.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/sku-event-strategy',
      name: 'skuEventStrategy',
      component: () => import('../views/SkuEventStrategy.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-event-strategy',
      name: 'vmEventStrategy',
      component: () => import('../views/VmEventStrategy.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/applied-event',
      name: 'appliedEvent',
      component: () => import('../views/AppliedEventStrategy.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/role',
      name: 'role',
      component: () => import('../views/RoleManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/user',
      name: 'user',
      component: () => import('../views/UserManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/sys-log',
      name: 'sysLog',
      component: () => import('../views/SysLogView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/settings',
      name: 'settings',
      component: () => import('../views/SettingsManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/customer',
      name: 'customer',
      component: () => import('../views/CustomerManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/partner',
      name: 'partner',
      component: () => import('../views/PartnerManage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/order',
      name: 'order',
      component: () => import('../views/OrderList.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-stock',
      name: 'vmStock',
      component: () => import('../views/VmStockView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-status',
      name: 'vmStatus',
      component: () => import('../views/VmStatusView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/vm-status-his',
      name: 'vmStatusHis',
      component: () => import('../views/VmStatusHisView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/About.vue'),
      meta: { requiresAuth: false },
    },
  ],
})

// Track if we've checked session on app initialization
let sessionChecked = false

// 全局路由守卫：检查会话状态，未登录拦截到登录页
router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()

  // On first navigation, check session status from backend
  if (!sessionChecked) {
    sessionChecked = true
    await userStore.checkSession()
  }

  if (to.meta.requiresAuth && !userStore.isLogin) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
