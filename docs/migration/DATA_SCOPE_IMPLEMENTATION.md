# 数据范围策略实现报告 (T-1.5)

**完成日期**: 2026-11-08  
**任务**: 建立可复用数据范围策略

---

## 1. 实现内容

### 1.1 DataScopeService

创建了通用的数据范围服务 `DataScopeService`，提供以下功能：

#### 1.1.1 getCompanyScopeExpression(user, companyPropertyPath)
根据用户公司层级生成 Cayenne 过滤表达式：

- **一级公司 (LEVEL_ONE)**：返回 `null`，表示不过滤，可查看所有数据
- **二级公司 (LEVEL_TWO)**：返回 `company IN (本公司 + 子公司)` 表达式
- **三级公司 (LEVEL_THREE)**：返回 `company = 本公司` 表达式

#### 1.1.2 canAccessCompany(user, targetCompany)
检查用户是否可以访问指定公司的数据。

#### 1.1.3 getAccessibleCompanyIds(user)
获取用户可以访问的公司 ID 列表。

### 1.2 VmsApiController 集成

修改了 `VmsApiController.listAllDeliveryTasks()` 方法，应用数据范围过滤：

```java
// 应用数据范围过滤
Expression scopeExpression = dataScopeService.getCompanyScopeExpression(user, "company");
if (scopeExpression != null) {
    expression = expression.andExp(scopeExpression);
}
```

---

## 2. 测试验证

### 2.1 测试数据

创建了完整的测试数据结构：

**公司层级**:
- 90001: Test Level 1 Company (一级公司)
  - 90002: Test Level 2 Company A (二级公司)
    - 90004: Test Level 3 Company A1 (三级公司)
    - 90005: Test Level 3 Company A2 (三级公司)
  - 90003: Test Level 2 Company B (二级公司)
    - 90006: Test Level 3 Company B1 (三级公司)

**测试用户**:
- test_level1 (公司 90001, 一级)
- test_level2a (公司 90002, 二级)
- test_level3a1 (公司 90004, 三级)

**测试工单**:
- 每个公司一个配送工单 (共 6 个)

### 2.2 测试结果

| 用户 | 公司层级 | 期望结果 | 实际结果 | 状态 |
|------|---------|---------|---------|------|
| test_level1 | 一级 | 所有 6 个工单 | 6 个工单 | ✓ 通过 |
| test_level2a | 二级 | 4 个工单 (自己 + 2 个子公司) | 4 个工单 | ✓ 通过 |
| test_level3a1 | 三级 | 1 个工单 (只有自己的) | 1 个工单 | ✓ 通过 |

### 2.3 测试脚本

- `database/test_data_scope.sql`: 创建测试数据
- `tools/test-data-scope.ps1`: 自动化测试脚本

---

## 3. 文件清单

### 新增文件
- `vms-service/src/main/java/com/kdx/vms/service/DataScopeService.java`
- `database/test_data_scope.sql`
- `tools/test-data-scope.ps1`

### 修改文件
- `vms-web/src/main/java/com/kdx/vms/web/controller/VmsApiController.java`

---

## 4. 使用方法

### 4.1 在 Controller 中使用

```java
@Resource
private DataScopeService dataScopeService;

@RequestMapping("/api/someEndpoint")
@ResponseBody
public String someEndpoint() {
    TSysUser user = getCurrentUser();
    
    // 生成过滤表达式
    Expression scopeExpression = dataScopeService.getCompanyScopeExpression(user, "company");
    
    // 应用到查询
    SelectQuery query = new SelectQuery(SomeEntity.class);
    Expression baseExpression = ExpressionFactory.matchExp("someField", someValue);
    if (scopeExpression != null) {
        baseExpression = baseExpression.andExp(scopeExpression);
    }
    query.setQualifier(baseExpression);
    
    List<SomeEntity> results = context.performQuery(query);
    // ...
}
```

### 4.2 检查访问权限

```java
if (!dataScopeService.canAccessCompany(user, targetCompany)) {
    throw new ForbiddenException("无权访问该公司数据");
}
```

---

## 5. 注意事项

1. **属性路径**: `companyPropertyPath` 参数必须是实体中公司属性的正确路径
   - 对于直接关联：`"company"`
   - 对于嵌套关联：`"task.company"` 或 `"vm.company"`

2. **延迟加载**: Cayenne 的关系属性使用延迟加载，确保在 ObjectContext 生命周期内访问

3. **性能**: 二级公司过滤使用 `IN` 表达式，对于大量子公司可能需要优化

---

## 6. 下一步

- 在其他需要数据范围过滤的 REST 端点中应用 DataScopeService
- 考虑添加基于用户角色的额外过滤（如只看到自己创建的工单）
- 性能优化：对于大量子公司的情况，考虑使用子查询而非 IN 列表

---

**状态**: ✅ T-1.5 已完成  
**测试**: ✅ 3/3 测试通过
