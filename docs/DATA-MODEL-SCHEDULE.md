# FlowMate 数据模型与日程页规范

更新时间：2026-05-31

## 目标

当前 MVP 需要把 Today、日程、Agent 建议、我的页工具连接统一到一套可扩展的数据模型中，避免页面继续依赖临时字符串。

本轮调整的核心结论：

- 任务时间不能再用单个 `time: String` 表示，必须拆成 `startAt` 和 `endAt`。
- 第二个底部导航页从“任务”调整为“日程”。
- 日程页作为连续月历流展示，从当前月开始向后滚动，并在日期格内用小字显示任务。
- 最近一周时间轴归入 Today 页，作为今日面板的辅助计划信息。
- Today 页、日程页和后续任务池都应复用同一套任务/日程显示模型。

## 时间模型

代码位置：`app/src/main/java/com/github/ezziy/model/FlowMateModels.kt`

### TaskTime

```kotlin
data class TaskTime(
    val startAt: LocalDateTime? = null,
    val endAt: LocalDateTime? = null,
)
```

显示规则：

| 情况 | 示例 | UI 文案 |
|---|---|---|
| 只有开始时间 | `startAt = 10:30` | `10:30 开始` |
| 只有结束时间 | `endAt = 18:00` | `18:00 截止` |
| 开始和结束都在同一天 | `10:30 - 12:00` | `10:30-12:00` |
| 开始和结束跨天 | `5月31日 22:00 - 6月1日 01:00` | `5月31日 22:00 - 6月1日 01:00` |
| 没有时间 | `null/null` | `未安排` |

判断任务属于哪一天：

- 有开始和结束：日期在 `[startAt.date, endAt.date]` 范围内都算发生。
- 只有开始：归属开始日期。
- 只有结束：归属截止日期。
- 都没有：不进入日历格和时间轴，只留在任务池或 Inbox。

## 任务模型

### TaskItem

```kotlin
data class TaskItem(
    val id: String,
    val title: String,
    val note: String? = null,
    val time: TaskTime = TaskTime(),
    val priority: TaskPriority = TaskPriority.Medium,
    val status: TaskStatus = TaskStatus.Inbox,
    val completedAt: LocalDateTime? = null,
    val tags: List<String> = emptyList(),
)
```

字段说明：

| 字段 | 说明 |
|---|---|
| `id` | 本地唯一 ID，后续可映射远端 ID。 |
| `title` | 任务主标题。 |
| `note` | 可选备注，用于详情页和 Agent 摘要。 |
| `time` | 开始/结束时间统一入口。 |
| `priority` | `High / Medium / Low`。 |
| `status` | `Inbox / Planned / InProgress / Completed / Deferred / Deleted`。 |
| `completedAt` | 用户完成任务的真实时间。只要存在，就优先判断为已完成。 |
| `tags` | 标签，后续用于课程、项目、来源分类。 |

任务显示策略：

- Today 优先任务只展示 `Planned` 或 `InProgress`。
- 已完成任务进入 Today Log 的复盘归档。
- 延后任务进入待重排池，后续在任务池或 Agent 调整中处理。
- 没有时间的任务不应强行显示在日程页月历里。

## 时间轴状态判断

`已开始` 不等于 `过去任务`。时间轴状态必须结合 `startAt`、`endAt`、`completedAt/status` 和当前时间 `now` 共同判断。

### TaskTimelineState

```kotlin
enum class TaskTimelineState {
    Unscheduled,
    NotStarted,
    Due,
    InProgress,
    Overdue,
    Completed,
}
```

判定规则：

| 时间字段 | 完成状态 | 当前时间位置 | 时间轴状态 |
|---|---|---|---|
| 任意 | `completedAt != null` 或 `status = Completed` | 任意 | `Completed` |
| 无开始/无结束 | 未完成 | 任意 | `Unscheduled`，不进时间轴 |
| 只有开始 | 未完成 | `now < startAt` | `NotStarted` |
| 只有开始 | 未完成 | `now >= startAt` | `InProgress` |
| 只有截止 | 未完成 | `now <= endAt` | `Due` |
| 只有截止 | 未完成 | `now > endAt` | `Overdue` |
| 开始 + 截止 | 未完成 | `now < startAt` | `NotStarted` |
| 开始 + 截止 | 未完成 | `startAt <= now <= endAt` | `InProgress` |
| 开始 + 截止 | 未完成 | `now > endAt` | `Overdue` |

时间轴展示顺序：

- 先展示正在进行或已逾期的任务。
- 再展示最近完成的 1 条任务。
- 最后展示未来一周内最近的任务，补足到最多 7 条。
- 进行中的任务不算过去任务，即使 `startAt` 已经早于当前时间。

## 日程模型

### ScheduleEntry

```kotlin
data class ScheduleEntry(
    val id: String,
    val title: String,
    val note: String? = null,
    val time: TaskTime,
    val source: ScheduleSource = ScheduleSource.Manual,
    val linkedTaskId: String? = null,
)
```

字段说明：

| 字段 | 说明 |
|---|---|
| `id` | 日程唯一 ID。 |
| `title` | 日程标题。 |
| `note` | 辅助说明。 |
| `time` | 日程必须有开始或结束时间。 |
| `source` | `Manual / Task / Calendar / Agent`。 |
| `linkedTaskId` | 如果日程来自任务，记录任务 ID。 |

日程页显示策略：

- 月历格：显示任务小字，当前实现最多展示 3 条，后续可加 “+N”。
- 月历流：从当前月开始连续向后展示月份，让日程拥有足够显示空间。
- 如果当前日期在当月 10 号之前，月历流会先展示上个月，方便回看刚过去的安排。
- 农历显示使用 Android ICU `ChineseCalendar`，不再使用按公历日期模拟的占位数据。
- 最近一周时间轴：移入 Today 页展示；进行中/逾期优先，已完成只保留最近 1 条，未来一周任务补足到最多 7 条。
- 任务生成的日程 `source = Task`，可回跳任务详情。
- 外部日历导入项 `source = Calendar`，未来只读展示，避免误删用户真实日历。

## Today 显示模型

### TodayPlanItem

```kotlin
data class TodayPlanItem(
    val taskId: String,
    val title: String,
    val time: TaskTime,
    val priority: TaskPriority,
    val isFocus: Boolean = false,
)
```

Today 页面只关心当天执行顺序，不应该承载完整任务编辑能力。

显示策略：

- Focus 取 `isFocus = true` 或当天最高优先级任务。
- Focus 卡片固定两行标题槽，避免轮播时高度抖动。
- 优先任务列表右滑完成，左滑延后。
- 过期任务如果是刚过期，优先进入 Today 顶部提醒。

## 过期提醒模型

### OverdueReminder

```kotlin
data class OverdueReminder(
    val taskId: String,
    val title: String,
    val overdueLabel: String,
    val previousPriority: TaskPriority,
    val recentlyExpired: Boolean,
)
```

显示策略：

- `recentlyExpired = true` 时，过期提醒上移到优先任务上方。
- Today 顶部 Focus 区可以在 Focus 与刚过期任务之间横向轮播。
- 过期任务仍显示原本优先级，不使用感叹号替代优先级。
- 支持完成、重排、删除。

## 我的页工具连接模型

### ToolConnection

```kotlin
data class ToolConnection(
    val id: String,
    val name: String,
    val accountHint: String?,
    val status: ConnectionStatus,
    val remainingQuotaLabel: String? = null,
)
```

显示策略：

- 只有 `已连接` 和 `待授权` 状态胶囊可点击。
- `remainingQuotaLabel` 用于展示剩余额度，例如 `本月 72%` 或 `剩余 120 次`。
- 工具连接本身不应该整行可点，避免误触。

## 页面落地状态

本轮已落地：

- 新增统一模型文件：`app/src/main/java/com/github/ezziy/model/FlowMateModels.kt`
- 新增日程页：`app/src/main/java/com/github/ezziy/ui/schedule/ScheduleScreen.kt`
- 底部第二导航从“任务”改为“日程”。
- Today 临时任务改用 `TaskTime` 显示开始/截止/时间段。
- 日程页改为连续月历流，最近一周时间轴移入 Today 页。

后续建议：

- 把 Today 页的私有 `TodayTask / OverdueTask / TaskPriority` 逐步替换为 `model` 包中的正式模型。
- 新增 Repository，把 sample 数据从 UI 中移出。
- 任务详情页保留，但入口应从日程或未来任务池进入，不再作为底部一级导航。
