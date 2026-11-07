# 本地文件存储改造报告（替代阿里云 OSS）

**日期**：2026-11-07  
**目标**：阿里云 OSS 停用后，用本地磁盘实现文件上传/读取/删除，业务代码零改动  
**结论**：✅ 打通，上传→落盘→HTTP 访问→删除全链路验证通过

---

## 一、改造范围

### 1.1 实际业务入口
盘点发现三个 OSS 类中，只有 `AliyunOssUtils` 被业务调用：
- `upload`：7 处（商品图、主题、升级包、资源等）
- `uploadToPrivate`：3 处（受益人证件等私有文件）
- `delete`：9 处
- `info`：**无业务调用**（OSS 图片信息查询，本地无等价，返回 null）

另两个类 `AliyunOss`、`AliyunOssImage` 只有硬编码路径的 `main` 调试方法，无业务引用，一并改造为本地存储演示。

### 1.2 设计原则
- **类名和 public 方法签名完全不变**，19 个业务调用点零改动
- 对象存储语义（bucket + key）映射为本地目录：
  - 公共 bucket → `local.storage.root/public`
  - 私有 bucket → `local.storage.root/private`
- key 自带目录前缀（如 `theme/123.zip`），直接映射为子目录

---

## 二、新增/改造文件

| 文件 | 说明 |
|------|------|
| `LocalFileStorage.java`（新增） | 本地存储核心：save/read/delete，含路径穿越防护 |
| `AliyunOssUtils.java`（重写） | 保持签名，内部委托 LocalFileStorage |
| `AliyunOss.java`、`AliyunOssImage.java`（简化） | 去掉 OSSClient，改为本地存储演示 main |
| `Md5Util.java`（改造） | 移除对 aliyun-sdk IOUtils 的依赖，改用 JDK 原生流 |
| `spring-mvc.xml` | 新增 `/vmsimgs/**` 静态资源映射到磁盘公共目录 |
| `application-sso.xml` | Shiro 放行 `/vmsimgs/**`（否则被拦截 302 到登录） |
| `setting.properties` | 新增 `local.storage.root` 等配置 |
| `LocalFileStorageTest.java`（新增） | 4 个测试：公共/私有/路径穿越/非法参数 |

改造后 **Java 代码中 `com.aliyun` 引用全部清除**。

---

## 三、关键问题与解决

### 3.1 中文路径导致目录名乱码（真实环境坑）
- 最初把存储根设为项目下的 `.../01_客户项目/.../local-data`
- properties 读取时中文编码错乱，实际落盘到名为 `01_??...?` 的异常目录
- **解决**：存储根改用无中文、无空格的 `D:/vms-data`
- **教训**：文件存储路径必须避开非 ASCII 字符，已在配置注释和文档标注

### 3.2 静态资源被 Shiro 拦截
- 配好 Spring 静态映射后，HTTP 访问 `/vmsimgs/...` 返回 302 跳登录
- 原因：Shiro filterChain 默认 `/** = authc`
- **解决**：在 `application-sso.xml` 增加 `/vmsimgs/** = anon`

### 3.3 路径穿越防护
- key 若含 `../../` 可越出 bucket 根目录
- `LocalFileStorage.resolveSafe` 规范化路径后校验 `target.startsWith(root)`，越界直接拒绝

---

## 四、配置

```properties
# 必须无中文、无空格
local.storage.root=D:/vms-data
local.storage.urlPrefix=http://127.0.0.1:8080/vmsimgs/
imgUrlPrefix=http://127.0.0.1:8080/vmsimgs/
```

目录结构：
```
D:/vms-data/
├── public/          # 公共 bucket（HTTP 可访问，映射 /vmsimgs/**）
│   ├── sku/
│   ├── theme/
│   └── upgradePackage/
└── private/         # 私有 bucket（不映射 HTTP，仅服务端读写）
    └── cert/
```

---

## 五、验证证据

### 5.1 单元/集成测试（4 个，全过）
- 公共 bucket 上传落盘、读回内容一致、删除生效
- 私有 bucket 与公共目录隔离
- `../` 路径穿越被拒绝
- 空 key、null 流等非法参数正确返回 false

### 5.2 HTTP 端到端
```
GET /vmsimgs/test/hello.txt        → 200，内容正确，content-type=text/plain
GET /vmsimgs/test/nope.txt         → 404（不是 302 登录跳转）
```

### 5.3 全量回归
全部 10 个集成测试通过（MySQL/设置树/Redis/RabbitMQ/工单状态机/库存扣减/本地存储），
`mvn compile` 成功，无 aliyun SDK 残留引用。

---

## 六、待办与注意

1. **生产部署**：本地磁盘不适合多实例/容器化环境，后续可平滑切换到 MinIO（S3 兼容）或自建对象存储；`LocalFileStorage` 的隔离使切换只需改一个类
2. **私有文件访问**：私有 bucket 无 HTTP 映射，如需下载要经 Controller 鉴权后流式输出（当前受益人证件等功能待联调时补）
3. **历史数据**：原 OSS 上的存量图片无法迁移（服务已停用、无备份），相关历史记录的图片链接会 404，属预期
4. **图片处理能力**：OSS 的 image/info、缩略图等服务本地不支持，若业务需要可引入图片处理库
5. 清理：测试中产生的中文乱码空目录（`01_??...?`）无害，可手工删除
