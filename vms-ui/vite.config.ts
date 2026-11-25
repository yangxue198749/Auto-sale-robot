import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// Vite 本地开发（5173）调用后端（8080），API 请求走 /api 代理前缀转发，
// 这样跨域、cookie（Shiro session）都由 Vite 透传，无需改后端。
export default defineConfig({
  plugins: [
    vue(),
    // Element Plus 按需引入：模板组件 / v-loading 指令由 Components resolver 注入，
    // ElMessage 等编程式 API 由 AutoImport 注入兜底（现有代码已深路径显式导入）。
    // components dts 关闭：开启会给 el-table 插槽引入严格类型，暴露 40+ 处历史
    // DefaultRow 不匹配（另立技术债卡处理，不在本卡范围）。
    AutoImport({ resolvers: [ElementPlusResolver()], dts: 'src/auto-imports.d.ts' }),
    Components({ resolvers: [ElementPlusResolver()], dts: false }),
  ],
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
  build: {
    rollupOptions: {
      output: {
        // 分包说明：不设 manualChunks——对象形式会把所列包的入口模块整个拉进
        // 依赖图（曾致 element-plus 全量 947kB）。按需引入后 Rollup 默认分包
        // 已把共享模块收敛到 ~268kB 以内的共享 chunk，无需手工干预。
      },
    },
  },
})
