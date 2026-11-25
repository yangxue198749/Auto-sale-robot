import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import './style.css'

// Element Plus 组件/指令/样式由 unplugin 按需注入（见 vite.config.ts），
// 不再全量 app.use(ElementPlus) —— 入口 chunk 从 ~1MB 降到 ~20kB。

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
