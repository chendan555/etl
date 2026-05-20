# Issue #3: 后端取消功能

## Parent

[PRD: 批量执行进度显示](../PRD.md)

## What to build

实现批量执行的取消功能。后端维护取消标志位，批量执行循环中检查标志位，如果收到取消命令则终止执行。通过 WebSocket 接收取消命令。

核心功能：
- 使用 `ConcurrentHashMap<String, AtomicBoolean>` 存储每个 taskId 的取消标志
- 批量执行循环中检查取消标志，如果为 true 则终止
- WebSocket 接收取消命令，设置对应的取消标志
- 推送"已取消"状态到前端
- 清理取消标志（执行完成后或取消后）

## Acceptance criteria

- [ ] 实现取消标志位存储（ConcurrentHashMap<String, AtomicBoolean>）
- [ ] 在批量执行循环中检查取消标志
- [ ] 收到取消命令时设置标志位为 true
- [ ] 终止执行后推送"cancelled"状态到前端
- [ ] 清理已完成或已取消任务的标志位
- [ ] 处理并发取消请求（线程安全）
- [ ] WebSocket 接收取消命令的消息格式：`{"action": "cancel", "taskId": "xxx"}`
- [ ] 编写单元测试验证取消功能

## Blocked by

- Issue #1: 后端 WebSocket 基础架构
- Issue #2: 后端批量执行异步化
