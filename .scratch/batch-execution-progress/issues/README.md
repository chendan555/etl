# Issues 索引

## 批量执行进度显示功能

共 8 个 Issues，按依赖顺序排列：

| Issue | 标题 | 类型 | 依赖 | 状态 |
|-------|------|------|------|------|
| #1 | [后端 WebSocket 基础架构](01-backend-websocket-infrastructure.md) | AFK | 无 | 待开始 |
| #4 | [前端批量进度 Tab UI](04-frontend-batch-progress-tab.md) | AFK | 无 | 待开始 |
| #2 | [后端批量执行异步化](02-backend-batch-execution-async.md) | AFK | #1 | 待开始 |
| #5 | [前端 WebSocket 连接管理](05-frontend-websocket-connection.md) | AFK | #1, #4 | 待开始 |
| #3 | [后端取消功能](03-backend-cancel-functionality.md) | AFK | #1, #2 | 待开始 |
| #6 | [前端进度显示](06-frontend-progress-display.md) | AFK | #4, #5 | 待开始 |
| #7 | [前端取消功能](07-frontend-cancel-functionality.md) | AFK | #3, #5, #6 | 待开始 |
| #8 | [前端日志清空逻辑](08-frontend-log-clear-logic.md) | AFK | #4 | 待开始 |

## 依赖关系图

```
#1 (后端 WebSocket)
├── #2 (后端异步化)
│   └── #3 (后端取消)
├── #5 (前端 WebSocket)
│   ├── #6 (前端进度显示)
│   │   └── #7 (前端取消)
│   └── #7 (前端取消)

#4 (前端 Tab UI)
├── #5 (前端 WebSocket)
├── #6 (前端进度显示)
└── #8 (前端日志清空)
```

## 建议执行顺序

1. **第一批**（可并行）：#1, #4
2. **第二批**：#2, #5, #8
3. **第三批**：#3, #6
4. **第四批**：#7
