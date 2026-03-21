**该文档由AI生成，供参考**

# Akane Agent 快速开始指南

## 5 分钟快速上手

### 1. 项目结构

```
akane/
├── src/main/java/com/bot/akane/
│   ├── agent/                    # Agent 核心
│   │   ├── Agent.java           # Agent 主类
│   │   ├── AgentManager.java    # Agent 工厂
│   │   ├── AgentState.java      # 状态枚举
│   │   ├── AgentStateManager.java # 状态管理
│   │   ├── IAgent.java          # Agent 接口
│   │   └── tools/               # 工具系统
│   ├── controller/              # 控制器
│   ├── service/                 # 业务服务
│   ├── exception/               # 异常体系
│   ├── util/                    # 工具类
│   └── config/                  # 配置类
├── ARCHITECTURE.md              # 架构文档
├── BEST_PRACTICES.md            # 最佳实践
└── README.md                    # 项目说明
```

### 2. 核心概念

#### Agent

- 智能对话引擎
- 支持工具调用
- 管理对话状态和历史

#### Tool

- 可被 Agent 调用的功能模块
- 支持超时和重试
- 支持元数据配置

#### State

- Agent 的生命周期状态
- IDLE → THINKING → EXECUTING → FINISHED
- 状态转换受严格验证

#### ReAct 循环

- Reasoning：思考是否需要调用工具
- Acting：执行工具
- Observing：观察结果，决定是否继续

### 3. 快速开始

#### 步骤 1：启动应用

```bash
mvn spring-boot:run
```

#### 步骤 2：发送聊天请求

```bash
curl -X POST http://localhost:1145/chat \
  -H "Content-Type: application/json" \
  -d '{
    "groupId": "group-1",
    "userInput": "你好，请告诉我现在的天气"
  }'
```

#### 步骤 3：查看响应

```json
{
  "code": 200,
  "message": "聊天成功",
  "data": {
    "groupId": "group-1",
    "reply": "根据天气工具的查询结果..."
  }
}
```

### 4. 创建自定义工具

```java
@Component
@Slf4j
@RequiredArgsConstructor
public class MyCustomTool implements ToolInterface {

    @Override
    public String getName() {
        return "MyCustomTool";
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
        log.info("Executing with param: {}", param);
        // 实现你的业务逻辑
        return "操作完成：" + param;
    }
}
```

### 5. 配置说明

#### application.yaml

```yaml
server:
  port: 1145

spring:
  application:
    name: akane
  datasource:
    url: jdbc:mysql://localhost:3306/akane
    username: root
    password: your_password
  ai:
    deepseek:
      api-key: your_api_key
      chat:
        options:
          model: deepseek-chat

Agent:
  name: akane-agent
  system-prompt: |
    你是一个接入QQ群的AI助手，你的名字是akane...

DEFAULT_MAX_MESSAGES: 20
```

### 6. 常见操作

#### 获取对话历史

```java
@Service
@RequiredArgsConstructor
public class HistoryService {
    private final AgentManager agentManager;

    public List<Message> getHistory(String groupId) {
        Agent agent = agentManager.getAgent(groupId);
        return agent != null ? agent.getConversationHistory() : Collections.emptyList();
    }
}
```

#### 重置对话

```java
public void resetChat(String groupId) {
    Agent agent = agentManager.getAgent(groupId);
    if (agent != null) {
        agent.reset();
    }
}
```

#### 处理异常

```java
try {
    String response = agentManager.chat(groupId, userInput);
} catch (AgentStateException e) {
    log.error("State error: {}", e.getErrorMessage());
} catch (ToolExecutionException e) {
    log.error("Tool error: {}", e.getToolName());
} catch (AgentException e) {
    log.error("Agent error: {}", e.getErrorCode());
}
```

### 7. 调试技巧

#### 启用调试日志

```yaml
logging:
  level:
    com.bot.akane.agent: DEBUG
    com.bot.akane.agent.tools: DEBUG
```

#### 查看追踪信息

日志中会自动包含：

- `traceId`：请求追踪 ID
- `sessionId`：会话 ID
- `groupId`：群组 ID

```
2024-01-01 12:00:00.000 INFO [main] [abc123def456] [session-1] [group-1] ...
```

#### 检查 Agent 状态

```java
Agent agent = agentManager.getAgent(groupId);
log.info("Current state: {}", agent.getState());
log.info("History size: {}", agent.getConversationHistory().size());
```

### 8. 性能优化建议

1. **设置合理的超时时间**

   ```java
   .timeoutMs(10000)  // 10 秒
   ```

2. **限制消息历史大小**

   ```yaml
   DEFAULT_MAX_MESSAGES: 20
   ```

3. **使用异步工具**

   ```java
   @Async
   public void processAsync(String data) {
       // 异步处理
   }
   ```

4. **启用 Agent 缓存**
   - AgentManager 自动缓存 Agent 实例
   - 避免重复创建

### 9. 测试

#### 运行单元测试

```bash
mvn test
```

#### 运行特定测试

```bash
mvn test -Dtest=AgentStateManagerTest
```

#### 运行集成测试

```bash
mvn test -Dtest=AgentIntegrationTest
```

### 10. 常见问题

**Q: 如何添加新工具？**
A: 创建实现 `ToolInterface` 的类，使用 `@Component` 注解，在方法上使用 `@Tool` 注解。

**Q: 如何处理工具超时？**
A: 在 `ToolMetadata` 中设置 `timeoutMs`，或使用 `ToolExecutionManager` 的重试机制。

**Q: 如何追踪请求？**
A: 使用 `TraceIdUtil` 设置追踪信息，日志中会自动包含。

**Q: 如何重置对话？**
A: 调用 `agent.reset()` 方法。

**Q: 如何处理并发请求？**
A: AgentManager 使用 Redis 锁自动处理并发，无需手动干预。

## 下一步

- 阅读 [架构文档](ARCHITECTURE.md) 了解详细设计
- 阅读 [最佳实践](BEST_PRACTICES.md) 学习最佳实践
- 查看 [测试用例](src/test/java) 了解使用示例

## 获取帮助

- 查看日志了解执行过程
- 启用调试日志获取更多信息
- 检查异常堆栈跟踪定位问题
