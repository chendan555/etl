# Claude Code 快速安装配置指南

> 下次在新项目中读到此文件，按顺序执行即可完成全部配置。

---

## 1. 权限设置（免确认）

编辑 `~/.claude/settings.json`，确保包含以下配置：

```json
{
  "permissions": {
    "defaultMode": "bypassPermissions",
    "allow": [
      "Bash(*)",
      "Read(*)",
      "Edit(*)",
      "Write(*)",
      "Glob(*)",
      "Grep(*)",
      "WebFetch(*)",
      "WebSearch(*)",
      "NotebookEdit(*)",
      "Agent(*)",
      "AskUserQuestion(*)",
      "TodoWrite(*)",
      "Skill(*)",
      "CronCreate(*)",
      "CronDelete(*)",
      "CronList(*)",
      "ScheduleWakeup(*)",
      "TaskOutput(*)",
      "TaskStop(*)",
      "EnterPlanMode(*)",
      "ExitPlanMode(*)",
      "EnterWorktree(*)",
      "ExitWorktree(*)"
    ]
  },
  "skipDangerousModePermissionPrompt": true
}
```

---

## 2. 安装 superpowers-zh（AI 编程超能力 · 中文版）

20 个 AI 编程方法论 skills，覆盖 TDD / 调试 / 代码审查 / 头脑风暴等。

```bash
cd /your/project
npx superpowers-zh
```

自动安装到 `.claude/skills/`，bootstrap 注入 `CLAUDE.md`。

**仓库**: https://github.com/jnMetaCode/superpowers-zh

---

## 3. 安装 agency-agents-zh（AI 智能体专家团队）

104 个即插即用的 AI 专家角色。按需保留部门。

```bash
# 克隆
git clone https://github.com/jnMetaCode/agency-agents-zh.git

# 手动复制（Node.js 版本过低时使用）
mkdir -p ~/.claude/agents
cp -r agency-agents-zh/{engineering,design,project-management,testing,specialized}/*.md ~/.claude/agents/
```

安装后默认 104 个装入 `~/.claude/agents`。按需裁剪：

```bash
cd ~/.claude/agents

# 保留指定部门（工程/设计/项目管理/测试/专项）
ls *.md | grep -v -E '^(engineering-|design-|project-management-|testing-|specialized-)' | xargs rm -f
```

**仓库**: https://github.com/jnMetaCode/agency-agents-zh

**使用说明**: 详见 [AGENTS-GUIDE.md](AGENTS-GUIDE.md)

---

## 4. 安装 mattpocock/skills（Matt Pocock 工程技能）

14 个实战工程 skills，包括 diagnose / grill-me / tdd / prototype 等。

```bash
cd /your/project
npx skills@latest add mattpocock/skills
```

安装到 `.claude/skills/`（与 superpowers-zh 共存）。

**仓库**: https://github.com/mattpocock/skills

---

## 5. 安装 Anthropic 官方 Skills

通过 `npx skills-installer` 安装 Anthropic 官方提供的 skills。

```bash
# 前端设计技能
npx skills-installer install @anthropics/claude-code/frontend-design --client claude-code
```

安装到 `.claude/skills/`，按需选择安装。

**可用 Skills**:
- `frontend-design` — 前端设计与 UI 开发

---

## 6. 配置 MCP 服务器

MCP（Model Context Protocol）服务器让 Claude Code 能够与外部工具交互。

### Chrome DevTools MCP

允许 Claude Code 控制 Chrome 浏览器进行前端调试。

创建 `~/.claude/.mcp.json`：

```json
{
  "mcpServers": {
    "chrome-devtools": {
      "command": "npx",
      "args": ["chrome-devtools-mcp@latest"]
    }
  }
}
```

**功能**: 截图、读取 DOM、查看控制台日志、检查网络请求

---

## 安装顺序总结

```
1. settings.json   → 权限免确认
2. npx superpowers-zh                    → 20 个方法论 skills
3. bash install.sh --tool claude-code    → AI 专家角色（按需裁剪）
4. npx skills@latest add mattpocock/skills → 14 个工程 skills
5. npx skills-installer install ...      → Anthropic 官方 skills
6. ~/.claude/.mcp.json                   → MCP 服务器配置
```

每步完成后重启 Claude Code 生效。

---

## 常见问题排查

### 问题 1: Node.js 版本过低导致 npx 安装失败

**症状**: `SyntaxError: The requested module 'fs' does not provide an export named 'rmSync'`

**原因**: Node.js v12 不支持 `rmSync`（需要 v14.14+）

**解决方案**: 使用 git clone 手动安装
```bash
git clone --depth 1 https://github.com/jnMetaCode/superpowers-zh.git /tmp/superpowers-zh
cp -r /tmp/superpowers-zh/skills/* .claude/skills/
```

### 问题 2: Skills 安装后不生效

**症状**: `/grill-me` 等命令无法识别

**原因**: 
1. 未创建 CLAUDE.md 注册 skills
2. Skills 目录结构不正确

**解决方案**:
1. 创建项目根目录的 `CLAUDE.md` 文件，列出所有可用 skills
2. 确保 skills 放在 `.claude/skills/` 根级别目录

### 问题 3: 嵌套目录的 skills 不被识别

**症状**: `.claude/skills/productivity/grill-me/SKILL.md` 不生效

**原因**: Claude Code 只识别 `.claude/skills/` 根级别的子目录，不支持多层嵌套

**解决方案**: 将嵌套目录的 skills 移到根级别
```bash
# ❌ 错误结构
.claude/skills/productivity/grill-me/SKILL.md

# ✅ 正确结构
.claude/skills/grill-me/SKILL.md
```

移动命令：
```bash
mv .claude/skills/productivity/grill-me .claude/skills/grill-me
mv .claude/skills/engineering/* .claude/skills/
```

### 问题 4: agency-agents-zh 安装脚本卡住

**症状**: `scripts/convert.sh` 长时间无响应

**解决方案**: 手动复制 agent 文件
```bash
git clone --depth 1 https://github.com/jnMetaCode/agency-agents-zh.git /tmp/agency-agents-zh
mkdir -p ~/.claude/agents
cp -r /tmp/agency-agents-zh/{engineering,design,project-management,testing,specialized}/*.md ~/.claude/agents/
```

### 问题 5: 重启后 skills 仍未生效

**检查清单**:
1. ✅ 确认 `.claude/skills/` 目录存在且包含 SKILL.md 文件
2. ✅ 确认项目根目录有 `CLAUDE.md` 文件
3. ✅ 确认 `settings.json` 中有 `"Skill(*)"` 权限
4. ✅ 完全关闭 Claude Code 并重新打开（不是新开会话）

### 正确的安装流程

```
1. 配置 ~/.claude/settings.json（权限免确认）
2. 手动克隆并复制 skills 到项目 .claude/skills/（扁平结构）
3. 手动克隆并复制 agents 到 ~/.claude/agents/
4. 创建项目 CLAUDE.md 文件注册所有 skills
5. 完全重启 Claude Code
```
