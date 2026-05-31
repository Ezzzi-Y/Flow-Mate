package com.github.ezziy.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class TaskPriority {
    High,
    Medium,
    Low,
}

enum class TaskStatus {
    Inbox,
    Planned,
    InProgress,
    Completed,
    Deferred,
    Deleted,
}

enum class TaskTimelineState(
    val label: String,
) {
    Unscheduled("未安排"),
    NotStarted("未开始"),
    Due("待完成"),
    InProgress("进行中"),
    Overdue("已逾期"),
    Completed("已完成"),
}

enum class ScheduleSource {
    Manual,
    Task,
    Calendar,
    Agent,
}

enum class ConnectionStatus {
    Connected,
    AuthorizationRequired,
    Disabled,
}

data class TaskTime(
    val startAt: LocalDateTime? = null,
    val endAt: LocalDateTime? = null,
) {
    fun primaryDate(): LocalDate? = startAt?.toLocalDate() ?: endAt?.toLocalDate()

    fun displayText(forDate: LocalDate? = null): String {
        val start = startAt
        val end = endAt
        val referenceDate = forDate ?: primaryDate()

        return when {
            start != null && end != null -> {
                if (referenceDate != null && start.toLocalDate() == referenceDate && end.toLocalDate() == referenceDate) {
                    "${start.toShortTime()}-${end.toShortTime()}"
                } else {
                    "${start.toMonthDayTime()} - ${end.toMonthDayTime()}"
                }
            }
            start != null -> "${start.toShortTime()} 开始"
            end != null -> "${end.toShortTime()} 截止"
            else -> "未安排"
        }
    }

    fun occursOn(date: LocalDate): Boolean {
        val start = startAt?.toLocalDate()
        val end = endAt?.toLocalDate()

        return when {
            start != null && end != null -> !date.isBefore(start) && !date.isAfter(end)
            start != null -> date == start
            end != null -> date == end
            else -> false
        }
    }
}

data class TaskItem(
    val id: String,
    val title: String,
    val note: String? = null,
    val time: TaskTime = TaskTime(),
    val priority: TaskPriority = TaskPriority.Medium,
    val status: TaskStatus = TaskStatus.Inbox,
    val completedAt: LocalDateTime? = null,
    val tags: List<String> = emptyList(),
) {
    fun timelineAnchor(): LocalDateTime? = completedAt ?: time.startAt ?: time.endAt

    fun timelineState(now: LocalDateTime): TaskTimelineState {
        if (status == TaskStatus.Completed || completedAt != null) {
            return TaskTimelineState.Completed
        }

        val start = time.startAt
        val end = time.endAt

        return when {
            start == null && end == null -> TaskTimelineState.Unscheduled
            start != null && end != null && now.isBefore(start) -> TaskTimelineState.NotStarted
            start != null && end != null && !now.isAfter(end) -> TaskTimelineState.InProgress
            start != null && end != null -> TaskTimelineState.Overdue
            start != null -> if (now.isBefore(start)) TaskTimelineState.NotStarted else TaskTimelineState.InProgress
            end != null -> if (now.isAfter(end)) TaskTimelineState.Overdue else TaskTimelineState.Due
            else -> TaskTimelineState.Unscheduled
        }
    }
}

data class ScheduleEntry(
    val id: String,
    val title: String,
    val note: String? = null,
    val time: TaskTime,
    val source: ScheduleSource = ScheduleSource.Manual,
    val linkedTaskId: String? = null,
)

data class TodayPlanItem(
    val taskId: String,
    val title: String,
    val time: TaskTime,
    val priority: TaskPriority,
    val isFocus: Boolean = false,
)

data class OverdueReminder(
    val taskId: String,
    val title: String,
    val overdueLabel: String,
    val previousPriority: TaskPriority,
    val recentlyExpired: Boolean,
)

data class ToolConnection(
    val id: String,
    val name: String,
    val accountHint: String?,
    val status: ConnectionStatus,
    val remainingQuotaLabel: String? = null,
)

private val shortTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val monthDayTimeFormatter = DateTimeFormatter.ofPattern("M月d日 HH:mm")

private fun LocalDateTime.toShortTime(): String = format(shortTimeFormatter)

private fun LocalDateTime.toMonthDayTime(): String = format(monthDayTimeFormatter)
