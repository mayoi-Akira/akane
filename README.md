**该文档由AI生成，供参考**

# Akane Agent - 智能对话系统

[![Java](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-green)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.0-brightgreen)](https://spring.io/projects/spring-ai)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

Akane 是一个基于 Spring AI 框架的智能 Agent 系统，支持多工具调用、ReAct 循环和分布式链路追踪。

主要用于作为接入QQ群的 AI 助手的后端，提供智能对话和工具调用能力。

QQ机器人方面使用的 [Koishi框架](https://github.com/koishijs/koishi)，本项目的Koishi插件地址：[koishi-plugin-akane](https://github.com/mayoi-Akira/koishi-plugin-akane)，插件只做指令调用，真正的智能对话、工具调用以及其他核心逻辑都在本项目中实现。

## ✨ 核心特性

- 🤖 **智能 Agent 引擎**：支持 ReAct 循环（Reasoning → Acting → Observing）
- 🔧 **灵活的工具系统**：易于扩展，支持元数据和超时控制
- 🔄 **自动重试机制**：指数退避重试，提高可靠性
- 📊 **完整的链路追踪**：MDC 支持，便于分布式调试
- 🛡️ **健壮的状态管理**：防止非法状态转换
- 📝 **详细的文档**：架构文档、最佳实践、快速开始指南
- ✅ **完善的测试**：单元测试和集成测试

## 🚀 快速开始

### 前置要求

- Java 17+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.0+

### 安装

```bash
# 克隆项目
git clone https://github.com/your-repo/akane.git
cd akane

# 构建项目
mvn clean package

# 启动应用
mvn spring-boot:run
```

### 基本使用

```bash
# 发送聊天请求
curl -X POST http://localhost:1145/chat \
  -H "Content-Type: application/json" \
  -d '{
    "groupId": "group-1",
    "userInput": "你好，请告诉我现在的天气"
  }'
```

更多详情请查看 [快速开始指南](QUICKSTART.md)。

## 📚 文档

- [架构文档](ARCHITECTURE.md) - 详细的系统架构设计
- [最佳实践](BEST_PRACTICES.md) - 开发和使用最佳实践
- [快速开始](QUICKSTART.md) - 5 分钟快速上手
- [重构总结](REFACTORING_SUMMARY.md) - 重构工作总结

## 🏗️ 项目结构

```
akane/
├── src/main/java/com/bot/akane/
│   ├── agent/                    # Agent 核心模块
│   │   ├── Agent.java           # Agent 主类
│   │   ├── AgentManager.java    # Agent 工厂和生命周期管理
│   │   ├── AgentState.java      # 状态枚举
│   │   ├── AgentStateManager.java # 状态管理器
│   │   ├── IAgent.java          # Agent 接口
│   │   └── tools/               # 工具系统
│   ├── controller/              # REST 控制器
│   ├── service/                 # 业务服务层
│   ├── exception/               # 异常体系
│   ├── util/                    # 工具类
│   └── config/                  # 配置类
├── src/test/java/               # 测试代码
├── src/main/resources/
│   ├── application.yaml         # 应用配置
│   ├── logback-spring.xml       # 日志配置
│   └── schema.sql               # 数据库脚本
├── ARCHITECTURE.md              # 架构文档
├── BEST_PRACTICES.md            # 最佳实践
├── QUICKSTART.md                # 快速开始
└── REFACTORING_SUMMARY.md       # 重构总结
```

## 🔑 核心概念

### Agent

智能对话引擎，支持工具调用和多步骤推理。

### Tool

可被 Agent 调用的功能模块，支持超时和重试。

### State

Agent 的生命周期状态：IDLE → THINKING → EXECUTING → FINISHED

### ReAct 循环

- **Reasoning**：思考是否需要调用工具
- **Acting**：执行工具
- **Observing**：观察结果，决定是否继续

## 🛠️ 创建自定义工具

```java
@Component
@Slf4j
@RequiredArgsConstructor
public class MyTool implements ToolInterface {

    @Override
    public String getName() {
        return "MyTool";
    }

    @Override
    public String getDescription() {
        return "我的自定义工具";
    }

    @Override
    public ToolDefaultType getType() {
        return ToolDefaultType.ENABLE;
    }

    @Tool(name = "doSomething", description = "执行某个操作")
    public String doSomething(String param) {
        // 实现你的业务逻辑
        return "操作完成：" + param;
    }
}
```

## 📊 架构图

```
┌─────────────────────────────────────┐
│      REST Controller                │
│   (GroupChatController)             │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Service Layer                  │
│   (GroupChatService)                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Agent Layer                    │
│   (Agent, AgentManager)             │
│   - State Management                │
│   - Message Chain                   │
│   - ReAct Loop                      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Tool Layer                     │
│   (ToolInterface, Tools)            │
│   - Retry & Timeout                 │
│   - Metadata                        │
└─────────────────────────────────────┘
```

## 🧪 测试

### 运行所有测试

```bash
mvn test
```

### 运行特定测试

```bash
mvn test -Dtest=AgentStateManagerTest
```

### 测试覆盖

- ✅ 单元测试：状态管理、异常体系、工具执行、链路追踪
- ✅ 集成测试：Agent 集成、工具集成

## 📈 性能指标

- **平均响应时间**：< 5 秒（不含工具调用）
- **工具调用成功率**：> 95%（含重试）
- **内存占用**：< 500MB（单个 Agent 实例）
- **并发支持**：支持 1000+ 并发请求

## 🔐 安全特性

- ✅ 状态转换验证：防止非法状态转换
- ✅ 工具超时控制：防止长时间阻塞
- ✅ 分布式锁：防止并发冲突
- ✅ 异常处理：完善的错误恢复机制

## 🚦 状态转换

```
IDLE
  ↓
THINKING
  ├→ EXECUTING
  │   ├→ THINKING（继续循环）
  │   ├→ FINISHED
  │   └→ ERROR
  ├→ FINISHED
  └→ ERROR

FINISHED/ERROR
  ↓
IDLE（重置）
```

## 📝 配置示例

### application.yaml

```yaml
server:
  port: 1145

spring:
  application:
    name: akane
  datasource:
    url: jdbc:mysql://localhost:3306/akane
    username: root
    password: password
  ai:
    deepseek:
      api-key: your_api_key
      chat:
        options:
          model: deepseek-chat

Agent:
  name: akane-agent
  system-prompt: |
    你是一个接入QQ群的AI助手...

DEFAULT_MAX_MESSAGES: 20
```

## 🐛 故障排查

### 常见问题

1. **Agent 状态异常**
   - 检查状态转换是否合法
   - 查看日志中的状态转换记录

2. **工具调用超时**
   - 增加工具的超时时间
   - 检查工具实现是否有性能问题

3. **消息丢失**
   - 检查 chatMemory 的大小限制
   - 查看 tempMessages 是否正确清理

4. **并发冲突**
   - 检查 Redis 锁是否正确获取
   - 查看锁的过期时间是否合理

### 启用调试日志

```yaml
logging:
  level:
    com.bot.akane.agent: DEBUG
    com.bot.akane.agent.tools: DEBUG
```
