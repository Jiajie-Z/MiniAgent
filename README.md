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

## Prompt 构造

`PromptBuilder` 负责把 Agent 运行所需信息组装成统一 prompt：

- ReAct 输出格式要求
- 当前可用工具清单
- 用户输入
- 已执行步骤和 Observation

这样 `AgentExecutor` 只负责流程控制，`ChatModel` 只负责根据 prompt 做下一步决策。后续接入真实大模型时，可以复用同一套 prompt 结构。

## 工具异常处理

工具执行由 `AgentExecutor` 统一包裹。工具抛出的运行时异常不会直接中断程序，而是被转换成 Observation：

```text
Observation: Tool Error: Only addition is supported now.
```

这样 Agent 可以把失败原因继续交给模型处理，而不是让整个应用崩溃。

## 运行

```powershell
javac -encoding UTF-8 -d target/classes (Get-ChildItem -Recurse src/main/java/*.java).FullName
java -cp target/classes com.jagent.App
```

启动后可以直接输入任务：

```text
> 现在几点了
> 帮我计算 12 + 30
> exit
```

也可以通过命令行参数执行单次任务：

```powershell
java -cp target/classes com.jagent.App "帮我计算 12 + 30"
```
