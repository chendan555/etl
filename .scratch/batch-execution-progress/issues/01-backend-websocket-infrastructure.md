# Issue #1: 后端 WebSocket 基础架构

## Parent

[PRD: 批量执行进度显示](../PRD.md)

## What to build

创建后端 WebSocket 基础架构，支持实时推送批量执行进度。实现 `BatchProgressWebSocket` 类，使用 `@ServerEndpoint` 注解创建 WebSocket 端点 `/ws/batchProgress/{taskId}`。该类需要管理客户端连接、维护 taskId 到 session 的映射、提供发送进度消息的方法，并处理连接关闭和异常情况。

核心功能：
- WebSocket 端点配置和连接管理
- taskId 到 WebSocket session 的映射维护
- 进度消息发送方法（支持 JSON 格式）
- 连接关闭和异常处理
- 支持多个客户端同时连接同一个 taskId

## Acceptance criteria

- [ ] 创建 `BatchProgressWebSocket` 类，使用 `@ServerEndpoint` 注解
- [ ] 实现 `@OnOpen`、`@OnClose`、`@OnError`、`@OnMessage` 方法
- [ ] 维护 `taskId -> Set<Session>` 的映射关系
- [ ] 提供 `sendProgress(String taskId, ProgressMessage message)` 方法
- [ ] 支持 JSON 格式的进度消息序列化
- [ ] 处理连接异常和关闭情况
- [ ] 编写单元测试验证 WebSocket 连接和消息发送

## Blocked by

None - 可以立即开始
