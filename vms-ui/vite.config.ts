import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Vite 本地开发（5173）调用后端（8080），API 请求走 /api 代理前缀转发，
// 这样跨域、cookie（Shiro session）都由 Vite 透传，无需改后端。
export default defineConfig({
  plugins: [vue()],
  // 生产构建产物要放进 Tomcat 的 /dist/ 下，使用相对路径引用 assets
  base: './',
  server: {
    port: 5173,
    proxy: {
      // /api/** → http://127.0.0.1:8080/**，去掉 /api 前缀
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
        // 关键：后端依赖 JSESSIONID / TOUCHARM_VMS cookie 做会话，代理必须透传
        cookieDomainRewrite: '',
      },
      // 静态文件（图片/主题）走本地存储
      '/vmsimgs': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: {
      '@': '/src',
    },
  },
})
