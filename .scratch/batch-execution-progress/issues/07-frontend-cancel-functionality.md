# Issue #7: 前端取消功能

## Parent

[PRD: 批量执行进度显示](../PRD.md)

## What to build

实现前端取消批量执行的功能。包括取消按钮的交互、发送取消命令到后端、显示"已取消"状态。

核心功能：
- 取消按钮在执行时启用，执行完成后禁用
- 点击取消按钮发送 WebSocket 消息：`{"action": "cancel", "taskId": "xxx"}`
- 接收"cancelled"状态后更新 UI
- 显示"已取消"提示
- 禁用取消按钮，启用执行按钮

## Acceptance criteria

- [ ] 取消按钮在批量执行时启用，执行完成后禁用
- [ ] 点击取消按钮发送取消命令到后端
- [ ] 取消命令格式：`{"action": "cancel", "taskId": "xxx"}`
- [ ] 接收"cancelled"状态后显示"已取消"提示
- [ ] 更新进度条显示"已取消"状态
- [ ] 禁用取消按钮，启用执行按钮
- [ ] 清理 WebSocket 连接
- [ ] 编写测试验证取消功能

## Blocked by

- Issue #3: 后端取消功能
- Issue #5: 前端 WebSocket 连接管理
- Issue #6: 前端进度显示
