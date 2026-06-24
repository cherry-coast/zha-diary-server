# AI 协作开发规范 (AI Coding Specification)

本规范旨在指导 AI 辅助编程工具（如 Copilot, Claude, Gemini, ChatGPT 等）以及人工开发者，在此项目中进行协同开发时遵循统一的代码标准、架构原则和最佳实践。

---

## 1. 项目整体架构与技术栈

项目是一个前后端分离的个人日记系统（ZhaDiary 🍉），分为前端客户端和后端服务：

### 后端架构
*   **技术栈**: Java 21, Spring Boot 3.5.3, Maven (多模块架构), MyBatis Plus 3.5.5, Caffeine (本地缓存), MySQL 8.4
*   **模块划分 (`zha-diary-server`)**:
    *   `base`: 基础通用模块。包含全局异常处理、公用工具类、统一响应体定义、通用常量、AOP切面、拦截器注解等。
    *   `core`: 核心配置模块。包含 Jackson 配置、异步线程池配置、SpringDoc/Knife4j 接口文档配置等。
    *   `model`: 数据模型定义模块。包含基础实体模型 `BaseModel`、前后端分离的 DTO / VO (如 `LoginParam`, `AdminLoginParam`)。
    *   `database`: 数据库操作与持久化模块。包含 MyBatis Plus 配置、自动填充处理器、SQL打印拦截器、以及 Mapper 接口。
    *   `service`: 业务逻辑接口及其实现。包含前台用户和后台管理员的独立业务。
    *   `web`: Web控制层。包含 Controller API 接口、拦截器（如 `AuthInterceptor`）、Web 配置以及 Spring Boot 启动类。

### 前端架构
*   **技术栈**: Vue 3 (Composition API `<script setup lang="ts">`), TypeScript 6.0.x, Vite 6.4.x, Sass.
*   **双端适配**: 页面视图区分桌面端 `src/views/web/` 和移动端 `src/views/mobile/`。

---

## 2. 后端开发规范 (Java / Spring Boot)

### 2.1 命名与目录规范
*   **基础包路径**: `com.cherry`
*   **组件扫描**: `@ComponentScan("com.cherry.*")`
*   **Mapper 扫描**: `@MapperScan("com.cherry.database.mapper")`，所有 Mapper 接口必须放置在 `database` 模块的 `com.cherry.database.mapper` 包下。
*   **类文件头注释**: 统一使用以下格式：
    ```java
    /**
     * @author cherry
     * @version 1.0.0
     * Description: [模块/类描述]
     * Date: [日期]
     * ClassName: [类名]
     * packageName: [包路径]
     */
    ```

### 2.2 统一响应与异常处理
*   **API 响应体**: 所有 Controller 层的接口必须返回 `CherryResponseEntity<T>` 统一响应格式。
    *   成功调用: `CherryResponseEntity.success(data)` 或 `CherryResponseEntity.success(data, msg)`
    *   失败调用: `CherryResponseEntity.fail(msg)` 或 `CherryResponseEntity.fail(code, msg)`
*   **异常抛出**:
    *   禁止直接在 Controller 抛出底层运行时异常。
    *   业务异常统一抛出 `CherryException`。可通过 `BaseExceptionEnum` 定义错误码。
    *   底层异常由 `GlobalExceptionHandler` 统一捕获。

### 2.3 认证、鉴权与前后台隔离
*   **缓存机制**: 本项目**未使用 Redis**，验证码、临时状态等需使用 `Caffeine` 本地缓存进行处理。
*   **前后台独立**: 前台用户（`user`）和后台管理员（`admin_user`）独立分表，接口路径也需严格区分。后台管理接口必须以 `/v1/admin/` 开头。
*   **全局拦截与白名单**: `AuthInterceptor` 默认拦截所有未登录请求。不需要校验 Token 的接口必须在类或方法上添加 `@AllowAnonymousAccess` 注解以实现匿名放行。
*   **JWT 签发与解析**: `TokenUtil.generateToken` 在签发 Token 时必须传入第三个参数 `userType`（`1`=前台用户，`2`=后台管理员），方便拦截器识别身份并防止 Token 混用越权访问。

### 2.4 数据库与 MyBatis-Plus 规范
*   **实体继承**: 所有主表实体类统一继承 `com.cherry.model.base.model.BaseModel`。
*   **自动填充**: 创建时间、更新时间和逻辑删除等字段通过自动填充拦截器处理，禁止在代码中手动 `setInsertTime()`。
*   **字段与表命名**: 遇到 SQL 关键字冲突时应在 `@TableField` 添加反单引号。

### 2.5 工具类使用规约
*   优先使用项目中已存在的工具类（扩展自 Hutool 或 Spring 的扩展类），避免重复造轮子或随意引入第三方依赖：
    *   字符串处理: `CherryStringUtil` (继承自 `StringUtils`)
    *   加密解密: `CherryAesUtil` (用于密码加密等)
    *   权限与登录校验: `TokenUtil` (JWT 操作，带有 userType)
    *   线程上下文: `UserContext` (存放当前登录用户状态 UserContext.User)

---

## 3. 前端开发规范 (Vue 3 / TypeScript)

### 3.1 编码风格与规范
*   **组件模式**: 统一采用 `<script setup lang="ts">` 组合式 API。
*   **类型声明**: 必须使用 TypeScript 进行强类型约束。公共类型定义在 `src/types/index.ts` 中并统一导出。

### 3.2 样式与 UI 设计
*   **CSS 与预处理器**: 项目使用 `Sass` 作为 CSS 预处理器。**严禁直接使用 TailwindCSS**，除非用户明确要求。请编写原生 CSS/Sass 保持高定制化与灵活性。
*   **视觉美学 (Aesthetics)**: 保持高级感和动态效果。UI 设计必须给用户留下深刻的第一印象，选用精致、协调的配色方案（本项目主色调：西瓜绿、西瓜红、深褐）。多使用微交互动画。

### 3.3 交互与 SEO
*   **语义化 HTML**: 使用 `header`, `aside`, `main`, `section` 等标签。
*   **SEO 最佳实践**: 确保每个独立页面视图在挂载时设定清晰的 `document.title`。对关键交互按钮设置唯一的、具有描述性的 `id`。

---

## 4. AI 编码与协同规则 (AI Coding Rules)

1.  **保持文档的完整性**: 在修改或重构已有代码时，**必须完整保留与修改无关的注释和方法说明**。
2.  **规划与执行模式**: 对于架构调整、新增模块等复杂任务，应先编写 `implementation_plan.md`，并在获得用户批准后才开始执行。
3.  **验证与测试**: 后端代码修改后必须运行 Maven 编译检查；前端代码修改后需运行 TypeScript 类型检查。
