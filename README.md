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

## 运行

```powershell
javac -encoding UTF-8 -d target/classes (Get-ChildItem -Recurse src/main/java/*.java).FullName
java -cp target/classes com.jagent.App
```
