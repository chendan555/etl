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

215 个即插即用的 AI 专家角色。按需保留部门。

```bash
# 克隆
git clone https://github.com/jnMetaCode/agency-agents-zh.git

# 安装到 Claude Code
cd agency-agents-zh && bash scripts/install.sh --tool claude-code
```

安装后默认 215 个全部装入 `~/.claude/agents`。按需裁剪：

```bash
cd ~/.claude/agents

# 保留指定部门（工程/设计/项目管理/测试/专项）
ls *.md | grep -v -E '^(engineering-|design-|project-management-|testing-|specialized-)' | xargs rm -f
```

**仓库**: https://github.com/jnMetaCode/agency-agents-zh

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

## 安装顺序总结

```
1. settings.json   → 权限免确认
2. npx superpowers-zh                    → 20 个方法论 skills
3. bash install.sh --tool claude-code    → AI 专家角色（按需裁剪）
4. npx skills@latest add mattpocock/skills → 14 个工程 skills
```

每步完成后重启 Claude Code 生效。
