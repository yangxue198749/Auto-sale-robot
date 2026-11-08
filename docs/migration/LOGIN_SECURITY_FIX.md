# 登录安全修复报告 (T-1.6)

**完成日期**: 2026-11-08  
**任务**: 修复登录绕过与账号状态处理

---

## 1. 完成内容

### 1.1 验证码日期绕过后门修复

**问题**: 原代码允许使用当天日期作为验证码绕过验证（开发便利，安全隐患）

**解决方案**: 
- 添加配置项 `login.captcha.date.bypass.enabled` (默认 false)
- 生产环境禁用日期绕过，必须使用真实验证码
- 开发/测试环境可配置启用

**修改文件**:
- `setting.properties`: 添加配置项
- `LoginController.java`: 读取配置并条件性允许绕过

### 1.2 敏感日志移除

**问题**: LoginController 记录验证码和用户认证状态到日志（安全风险）

**解决方案**: 移除以下日志输出：
- 用户输入的验证码
- Session 中的验证码
- Shiro 认证状态

### 1.3 锁定账号处理

**问题**: 锁定账号仍可通过 /me 端点获取信息

**解决方案**: 
- AuthApiController 的 /me 端点检查账号锁定状态
- 锁定账号返回 401 并强制登出

**修改文件**:
- `AuthApiController.java`: 添加锁定检查逻辑

---

## 2. 测试验证

### 2.1 验证码测试

**测试 1: 错误验证码**
```bash
$ curl -X POST http://127.0.0.1:8080/submitLogin -d "userName=admin&password=1&validCode=wrong"
{"code":4,"message":"您输入的验证码有误，请重新输入."}
```
✓ 正确拒绝

**测试 2: 日期绕过（生产配置）**
```bash
$ TODAY=$(date +%Y-%m-%d)
$ curl -X POST http://127.0.0.1:8080/submitLogin -d "userName=admin&password=1&validCode=$TODAY"
{"code":4,"message":"您输入的验证码有误，请重新输入."}
```
✓ 正确拒绝（生产环境禁用绕过）

### 2.2 配置说明

**生产环境** (`setting.properties`):
```properties
login.captcha.date.bypass.enabled=false
```

**开发/测试环境**:
```properties
login.captcha.date.bypass.enabled=true
```

---

## 3. 文件清单

### 修改文件
- `vms-web/src/main/resources/setting.properties`
- `vms-web/src/main/java/com/kdx/vms/web/controller/LoginController.java`
- `vms-web/src/main/java/com/kdx/vms/web/controller/AuthApiController.java`

### 测试文件
- `tools/test-login-security.ps1`

---

## 4. 安全改进

1. ✅ 生产环境禁用验证码日期绕过
2. ✅ 移除敏感日志输出
3. ✅ 锁定账号在 /me 端点返回 401
4. ✅ 锁定账号强制登出

---

## 5. 向后兼容性

- 现有测试脚本如果使用日期绕过，需要：
  1. 修改 `setting.properties` 设置 `login.captcha.date.bypass.enabled=true`
  2. 或者改用真实验证码流程

---

**状态**: ✅ T-1.6 已完成  
**测试**: ✅ 生产环境安全验证通过
