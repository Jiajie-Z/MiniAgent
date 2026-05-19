# JAgent

一个从零实现的轻量级 Java Agent，用来学习并展示 Agent 的核心执行机制。

## 当前目标

第一阶段先实现 Agent 内核：

- 接收用户任务
- 让模型决定下一步动作
- 调用工具
- 把工具结果写回上下文
- 循环直到输出最终答案

当前版本使用 `RuleBasedChatModel` 模拟大模型，方便先理解 Agent 的执行流程。后续再接真实大模型、SSE、RAG 和日志落库。

## ReAct 格式

当前 Agent 使用类似 ReAct 的文本格式描述模型决策：

```text
Thought: The user needs a calculation.
Action: calculator
Action Input: 12+30
```

工具执行完成后，Agent 会把结果作为 Observation 写入上下文，再让模型继续决策：

```text
Observation: 42
Final Answer: Tool calculator returned: 42
```

这个设计的意义是：后续接入真实大模型时，只需要让模型按同样格式输出，Agent 执行器仍然可以复用。

## 工具注册

每个工具都实现统一的 `Tool` 接口：

```java
public interface Tool {
    String name();
    String description();
    String parametersSchema();
    String execute(String arguments);
}
```

`ToolRegistry` 负责注册工具、按名称查找工具，并渲染工具说明。真实大模型接入后，工具说明会被放进 prompt，让模型知道可以调用哪些工具，以及每个工具需要什么参数。

## 运行

```powershell
javac -encoding UTF-8 -d target/classes (Get-ChildItem -Recurse src/main/java/*.java).FullName
java -cp target/classes com.jagent.App
```
