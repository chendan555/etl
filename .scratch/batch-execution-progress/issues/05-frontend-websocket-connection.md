# Issue #5: 前端 WebSocket 连接管理

## Parent

[PRD: 批量执行进度显示](../PRD.md)

## What to build

实现前端 WebSocket 连接管理，在批量执行时建立连接，接收后端推送的进度消息。处理连接建立、消息接收、连接断开和重连逻辑。

核心功能：
- 调用 `execBatchRun()` 获取 taskId 后建立 WebSocket 连接
- 连接地址：`ws://localhost:8080/ws/batchProgress/{taskId}`
- 接收 JSON 格式的进度消息并解析
- 处理连接断开事件，提示用户刷新页面
- 提供重连机制（可选）
- 执行完成后可选择断开连接

## Acceptance criteria

- [ ] 实现 `connectWebSocket(taskId)` 方法建立连接
- [ ] 监听 `onopen`、`onmessage`、`onclose`、`onerror` 事件
- [ ] 解析 JSON 格式的进度消息
- [ ] 将解析后的数据传递给进度显示组件
- [ ] 处理连接断开事件，显示提示信息
- [ ] 实现简单的重连机制（失败后提示刷新）
- [ ] 执行完成后断开 WebSocket 连接
- [ ] 处理网络异常和服务器错误
- [ ] 编写测试验证 WebSocket 连接和消息接收

## Blocked by

- Issue #1: 后端 WebSocket 基础架构
- Issue #4: 前端批量进度 Tab UI
