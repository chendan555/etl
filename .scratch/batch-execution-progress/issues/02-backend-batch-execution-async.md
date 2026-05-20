# Issue #2: 后端批量执行异步化

## Parent

[PRD: 批量执行进度显示](../PRD.md)

## What to build

修改 `FlowController.batchRun()` 方法为异步执行。当用户触发批量执行时，立即返回一个 taskId，后台线程继续执行批量任务。每完成一批次，通过 WebSocket 推送进度消息到前端。

核心改动：
- `batchRun()` 方法改为异步，使用 `@Async` 或 `CompletableFuture`
- 生成唯一的 taskId（UUID）
- 调用 `BatchProgressWebSocket.sendProgress()` 推送进度
- 记录每批次的执行状态、耗时、影响行数
- 计算整体进度、成功/失败数量、预计剩余时间

## Acceptance criteria

- [ ] 修改 `batchRun()` 方法返回 taskId（而非等待执行完成）
- [ ] 实现异步执行逻辑，使用线程池管理后台任务
- [ ] 生成唯一的 taskId（UUID 格式）
- [ ] 每批次完成后调用 WebSocket 推送进度消息
- [ ] 进度消息包含：当前批次、总批次、成功数、失败数、耗时、预计剩余时间
- [ ] 记录每批次的详细信息：参数值、状态、耗时、影响行数、错误信息
- [ ] 计算并更新整体进度百分比
- [ ] 执行完成后推送最终状态（completed）
- [ ] 编写单元测试验证异步执行和进度推送

## Blocked by

- Issue #1: 后端 WebSocket 基础架构
