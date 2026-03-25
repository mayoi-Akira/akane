# Akane 快速开始

本文档面向首次拉起本项目的开发者，按步骤执行可在本地完成启动和接口验证。

## 1. 环境要求

- Java 17+
- Maven 3.9+
- MySQL 5.7+
- Redis 6+

## 2. 初始化配置

在项目根目录复制环境变量模板：

Linux/macOS:

```bash
cp .env.example .env
```

Windows CMD:

```cmd
copy .env.example .env
```

最少需要填写以下变量：

- SPRING_DATASOURCE_URL
- MYSQL_APP_USER
- MYSQL_ROOT_PASSWORD
- DEEPSEEK_API_KEY
- WEATHER_API_KEY
- WEATHER_API_URL

可选变量（有默认值）：

- SERVER_PORT（默认 1145）
- SPRING_PROFILES_ACTIVE（默认 dev）
- SPRING_CACHE_TYPE（默认 redis）
- REDIS_HOST（默认 localhost）
- REDIS_PORT（默认 6379）
- REDIS_DATABASE（默认 1）
- DEEPSEEK_MODEL（默认 deepseek-chat）

## 3. 启动应用

```bash
mvn clean package
mvn spring-boot:run
```

启动后访问地址：

- http://localhost:1145

## 4. 接口冒烟验证

### 4.1 聊天接口

```bash
curl -X POST http://localhost:1145/chat \
  -H "Content-Type: application/json" \
  -d '{
    "groupId": "group-1",
    "messageId": "msg-1001",
    "userId": "user-42",
    "userMessage": "现在几点了？"
  }'
```

### 4.2 查询群工具配置

```bash
curl "http://localhost:1145/tools/list?groupId=group-1"
```

### 4.3 更新群工具配置

```bash
curl -X POST http://localhost:1145/tools/update \
  -H "Content-Type: application/json" \
  -d '{
    "groupId": "group-1",
    "toolCodes": ["weather", "time"],
    "enable": true
  }'
```

### 4.4 重置 Agent

```bash
curl "http://localhost:1145/agent/reset?groupId=group-1&messageId=msg-1002"
```

## 5. 运行测试

```bash
mvn test
```

运行单个测试类：

```bash
mvn test -Dtest=AgentIntegrationTest
```

## 6. 常见问题

1. 启动时报数据库初始化失败：检查 src/main/resources/schema.sql 是否每条 SQL 都以分号结尾。
2. Windows 终端中文日志乱码：执行 chcp 65001 后再启动。
3. 连接 MySQL 被拒绝：优先使用专用业务账号，不建议 root 直接对外连接。
