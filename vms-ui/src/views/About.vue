<template>
  <div class="about">
    <h1>关于</h1>
    <p>
      本前端是 VMS 售货机管理系统的 Vue 3 重构版，替代原 JSP 页面。
      后端保持 Spring MVC + Shiro 不变，通过 Vite 代理透传 cookie 做会话保持。
    </p>
    <ul>
      <li>技术栈：Vue 3 + TypeScript + Vite + Vue Router + Pinia</li>
      <li>开发端口：http://localhost:5173</li>
      <li>后端端口：http://127.0.0.1:8080</li>
      <li>API 代理：/api/* → 后端（去掉 /api 前缀）</li>
    </ul>
  </div>
</template>

<style scoped>
.about {
  max-width: 600px;
}
.about h1 {
  margin-bottom: 16px;
}
.about p {
  margin-bottom: 12px;
  line-height: 1.6;
}
.about ul {
  list-style: none;
  margin-top: 16px;
}
.about li {
  padding: 8px 0;
  border-bottom: 1px solid #bdc3c7;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}
</style>
