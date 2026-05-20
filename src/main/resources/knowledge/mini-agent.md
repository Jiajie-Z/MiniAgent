# MiniAgent

MiniAgent is a lightweight Java Agent Runtime. It supports a ReAct-style Thought, Action, Observation, Final Answer loop, tool registration, multi-step task execution, SSE event streaming, execution logs, unified REST API responses, input validation, and Docker deployment.

## Tools

MiniAgent provides a Tool interface and ToolRegistry. Built-in tools include time lookup, simple calculator, and rag_search. Tool errors are converted into Observation messages so the Agent can continue reasoning or explain failures.

## RAG

The RAG module contains document chunks, a text splitter, a simple embedding model, an in-memory vector store, and a retriever. The rag_search tool exposes retrieval results to the Agent as an Observation.
