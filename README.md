# Akane

Akane 是一个基于 Spring Boot + Spring AI 的群聊 AI 助手后端，支持多工具调用、群级工具开关、上下文管理与 Agent 重置。

## 技术栈

- Java 17
- Spring Boot 3.5.8
- Spring AI 1.1.0（DeepSeek）
- MySQL + MyBatis
- Redis（缓存/并发控制）

## 运行前准备

1. 安装 Java 17+ 与 Maven 3.9+。
2. 准备 MySQL 与 Redis。
3. 复制环境变量模板并填写：

```bash
cp .env.example .env
```

Windows CMD:

```cmd
copy .env.example .env
```

项目会通过 spring.config.import 自动读取根目录 .env。

## 关键环境变量

以下变量与 src/main/resources/application.yaml 一一对应：

- SERVER_PORT: 服务端口，默认 1145
- SPRING_PROFILES_ACTIVE: 运行环境，默认 dev
- SPRING_CACHE_TYPE: 缓存类型，默认 redis
- SPRING_DATASOURCE_URL: MySQL 连接串
- MYSQL_APP_USER: 数据库用户名
- MYSQL_ROOT_PASSWORD: 数据库密码
- REDIS_HOST: Redis 主机
- REDIS_PORT: Redis 端口
- REDIS_DATABASE: Redis 数据库索引
- EMAIL_HOST / EMAIL_PORT / EMAIL_USERNAME / EMAIL_PASSWORD: SMTP 配置
- DEEPSEEK_API_KEY: DeepSeek API Key
- DEEPSEEK_MODEL: 模型名，默认 deepseek-chat
- WEATHER_API_KEY / WEATHER_API_URL: 天气接口配置

建议优先使用专用数据库账号，不要使用 root 直连业务。

## 启动项目

```bash
mvn clean package
mvn spring-boot:run
```

默认地址：

- http://localhost:1145

## API 快速验证

### 1) 聊天接口

- 路径: POST /chat

请求示例：

```json
{
  "groupId": "group-1",
  "messageId": "msg-1001",
  "userId": "user-42",
  "userMessage": "现在几点了？"
}
```

### 2) 查询群工具配置

- 路径: GET /tools/list?groupId=group-1

### 3) 更新群工具开关

- 路径: POST /tools/update

请求示例：

```json
{
  "groupId": "group-1",
  "toolCodes": ["weather", "time"],
  "enable": true
}
```

### 4) 重置 Agent

- 路径: GET /agent/reset?groupId=group-1&messageId=msg-1002

## 测试

运行所有测试：

```bash
mvn test
```

运行单个测试类：

```bash
mvn test -Dtest=AgentStateManagerTest
```

## 项目结构

```text
src/main/java/com/bot/akane
  ├─ agent            Agent 核心逻辑
  ├─ controller       Web 接口
  ├─ service          业务服务
  ├─ mapper           MyBatis Mapper
  ├─ model            请求/响应/实体模型
  ├─ config           配置类
  └─ util             工具类

src/main/resources
  ├─ application.yaml
  ├─ application-dev.yaml
  ├─ schema.sql
  ├─ db/migration
  └─ mapper
```
