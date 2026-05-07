# novelplatform-v

小说阅读平台后端服务，基于 Spring Boot 3 构建的 RESTful API 系统，支持用户管理、小说管理、章节管理、书架管理和评论管理，同时集成微信小程序登录和双 JWT 认证机制。

## 技术栈

| 层次 | 技术 | 说明 |
|------|------|------|
| 语言 | Java 17 | |
| 框架 | Spring Boot 3.5.7 | Jakarta EE |
| 数据库 | MySQL | utf8mb4 字符集 |
| ORM | MyBatis 3.0.3 | Mapper XML |
| 缓存 | Redis | Refresh Token 存储与吊销 |
| 安全 | Spring Security + JWT | 双 Token 认证（Access + Refresh） |
| JWT 库 | jjwt 0.11.5 | HMAC-SHA256 |
| 微信登录 | weixin-java-miniapp 4.6.0 | 微信小程序登录集成 |
| 构建工具 | Maven | Maven Wrapper |
| 工具库 | Lombok | 实体类注解 |

## 项目结构

```
novelplatform-v/
├── src/main/java/org/example/novelplatform/
│   ├── NovelplatformVApplication.java    # 启动类
│   ├── config/                           # 配置类
│   │   ├── JwtAuthenticationFilter.java  # JWT 请求过滤器
│   │   ├── RedisConfig.java              # Redis 配置
│   │   ├── SecurityConfig.java           # Spring Security + CORS 配置
│   │   └── WechatMiniAppConfig.java      # 微信小程序配置
│   ├── controller/                       # 控制器层
│   │   ├── AuthController.java           # Token 刷新 & 登出
│   │   ├── BookshelfController.java      # 书架接口
│   │   ├── ChapterController.java        # 章节接口
│   │   ├── CommentController.java        # 评论接口
│   │   ├── NovelController.java          # 小说接口
│   │   ├── UserController.java           # 用户接口
│   │   └── WechatLoginController.java    # 微信登录接口
│   ├── entity/                           # 实体类
│   │   ├── User.java / Novel.java / Chapter.java
│   │   ├── Bookshelf.java / Comment.java
│   │   └── dto/UserDTO.java
│   ├── mapper/                           # MyBatis Mapper 接口
│   ├── service/                          # 业务逻辑层（接口 + impl）
│   └── util/                             # 工具类
│       ├── JwtUtil.java                  # JWT 令牌生成与校验
│       ├── PasswordEncoder.java          # BCrypt 密码加密
│       ├── RedisUtil.java                # Redis 操作封装
│       └── ResponseMessage.java          # 统一响应格式
├── src/main/resources/
│   ├── application.yml                   # 主配置文件
│   └── mapper/                           # MyBatis XML 映射文件
├── uploads/
│   ├── avatars/                          # 用户头像上传目录
│   └── covers/                           # 小说封面上传目录
├── 数据库.sql                             # 数据库建表脚本
├── 接口文档.md                            # API 接口文档（中文）
├── 设计文档.md                            # 认证系统设计文档
└── 微信目录结构.md                        # 微信小程序前端结构
```

## 快速开始

### 环境要求

- **JDK 17** 或更高版本
- **MySQL** 8.0+（需创建数据库 `novelplatform`）
- **Redis** 7.0+
- **Maven**（项目内置 Maven Wrapper，无需手动安装）

### 安装与运行

1. **克隆项目**

```bash
git clone <repository-url>
cd novelplatform-v
```

2. **初始化数据库**

在 MySQL 中创建数据库并导入表结构：

```sql
CREATE DATABASE IF NOT EXISTS novelplatform DEFAULT CHARACTER SET utf8mb4;
USE novelplatform;
SOURCE 数据库.sql;
```

3. **修改配置**

编辑 `src/main/resources/application.yml`，根据本地环境修改数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/novelplatform
    username: root
    password: 你的密码
  data:
    redis:
      host: localhost
      port: 6379
```

4. **启动项目**

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

启动后访问 `http://localhost:8080`。

### 打包部署

```bash
./mvnw clean package -DskipTests
java -jar target/novelplatform-v-0.0.1-SNAPSHOT.jar
```

## 功能模块

### 用户管理
- 用户注册、登录（密码 + 微信小程序）
- 个人信息 CRUD
- 头像上传
- 双 JWT 令牌认证（Access Token 30 分钟，Refresh Token 7 天）

### 小说管理
- 小说 CRUD、搜索
- 封面上传
- 点击量 / 收藏量 / 推荐量 / 评分统计
- 热门推荐、最新上架排行榜

### 章节管理
- 章节 CRUD（支持批量删除）
- 按小说 / 章节号查询
- 字数统计

### 书架管理
- 添加 / 移除收藏
- 阅读进度追踪（记录最后阅读章节）
- 批量操作

### 评论管理
- 评论 CRUD（支持回复嵌套，分页查询）
- 点赞 / 取消点赞
- 批量删除

## API 统一响应格式

所有接口返回统一 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

完整 API 文档请参阅 [接口文档.md](./接口文档.md)。

## 认证流程

```
登录 → 返回 Access Token + Refresh Token
请求 → 请求头携带 Access Token
过期 → 用 Refresh Token 换取新的 Access Token
登出 → 吊销 Refresh Token（Redis 黑名单）
```

详细设计说明请参阅 [设计文档.md](./设计文档.md)。

## 数据库表

| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `novel` | 小说表 |
| `chapter` | 章节表 |
| `bookshelf` | 书架表 |
| `comment` | 评论表 |

## 微信小程序集成

项目配套微信小程序前端，目录结构请参阅 [微信目录结构.md](./微信目录结构.md)。

微信配置文件位于 `application.yml`，需添加：

```yaml
wechat:
  miniapp:
    app-id: 你的AppID
    app-secret: 你的AppSecret
```

## 安全说明

- JWT 密钥通过 `@Value` 默认值提供，**生产环境请通过环境变量或配置中心注入**
- 密码使用 BCrypt 加密存储
- Refresh Token 支持服务端吊销（Redis 黑名单）
- 文件上传限制为 5MB
