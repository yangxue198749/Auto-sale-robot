import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
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
