@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.github.ezziy.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.ezziy.ui.components.AuroraBackground
import com.github.ezziy.ui.components.GlassCard
import com.github.ezziy.ui.theme.AppleBlue
import com.github.ezziy.ui.theme.AppleDivider
import com.github.ezziy.ui.theme.AppleRed
import com.github.ezziy.ui.theme.AppleSurfaceDim
import com.github.ezziy.ui.theme.AppleTextPrimary
import com.github.ezziy.ui.theme.AppleTextSecondary
import com.github.ezziy.ui.theme.FlawMateTheme
import com.github.ezziy.model.TaskItem
import com.github.ezziy.model.TaskStatus
import com.github.ezziy.model.TaskTime
import com.github.ezziy.model.TaskTimelineState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

private data class TodayTask(
    val id: String,
    val title: String,
    val time: TaskTime = TaskTime(),
    val priority: TaskPriority,
)

private data class TodayEvent(
    val anchorAt: LocalDateTime,
    val title: String,
    val note: String,
    val time: TaskTime,
    val state: TaskTimelineState,
)

private data class OverdueTask(
    val id: String,
    val title: String,
    val dueLabel: String,
    val priority: TaskPriority,
    val recentlyExpired: Boolean,
)

private enum class TaskPriority(
    val label: String,
    val color: Color,
) {
    High("高", AppleRed),
    Medium("中", AppleBlue),
    Low("低", AppleTextSecondary),
}

@Composable
fun HomeScreen(onOpenAgent: () -> Unit = {}) {
    val now = remember { LocalDateTime.now() }
    val today = remember { LocalDate.now() }
    val dateLabel = remember(today) { today.format(DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINA)) }
    val activeTasks = remember {
        mutableStateListOf(
            TodayTask(
                id = "today-ui",
                title = "完成 Today 页面首版开发",
                time = TaskTime(startAt = today.atTime(10, 30), endAt = today.atTime(12, 0)),
                priority = TaskPriority.High,
            ),
            TodayTask(
                id = "task-model",
                title = "整理任务数据模型字段",
                time = TaskTime(endAt = today.atTime(14, 0)),
                priority = TaskPriority.Medium,
            ),
            TodayTask(
                id = "agent-entry",
                title = "复查 Agent 工具调用入口",
                time = TaskTime(startAt = today.atTime(17, 30)),
                priority = TaskPriority.Low,
            ),
        )
    }
    val completedTasks = remember { mutableStateListOf<TodayTask>() }
    val deferredTasks = remember { mutableStateListOf<TodayTask>() }
    val rescheduledTasks = remember { mutableStateListOf<OverdueTask>() }
    val overdueTasks = remember {
        mutableStateListOf(
            OverdueTask("task-detail", "补全任务详情交互", "刚刚过期", TaskPriority.High, recentlyExpired = true),
            OverdueTask("review-stats", "整理复盘统计字段", "昨天 18:00 截止", TaskPriority.Medium, recentlyExpired = false),
        )
    }
    val events = remember(now) { sampleWeekEvents(now) }
    val plannedCount = activeTasks.size + completedTasks.size + deferredTasks.size
    val progress = if (plannedCount == 0) 1f else completedTasks.size / plannedCount.toFloat()

    AuroraBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                TodayBriefCard(
                    today = today,
                    dateLabel = dateLabel,
                    completedCount = completedTasks.size,
                    activeCount = activeTasks.size,
                    progress = progress,
                    focusTask = activeTasks.firstOrNull(),
                    urgentOverdueTask = overdueTasks.firstOrNull { it.recentlyExpired },
                    onOpenAgent = onOpenAgent,
                )
                if (overdueTasks.any { it.recentlyExpired }) {
                    OverdueCard(
                        tasks = overdueTasks,
                        onComplete = { task ->
                            overdueTasks.removeAll { it.id == task.id }
                            completedTasks.add(0, TodayTask(id = task.id, title = task.title, priority = task.priority))
                        },
                        onReschedule = { task ->
                            overdueTasks.removeAll { it.id == task.id }
                            rescheduledTasks.add(0, task)
                        },
                        onDelete = { task ->
                            overdueTasks.removeAll { it.id == task.id }
                        },
                    )
                }
                PriorityTasksCard(
                    tasks = activeTasks,
                    onCompleteTask = { task ->
                        activeTasks.removeAll { it.id == task.id }
                        completedTasks.add(0, task)
                    },
                    onDeferTask = { task ->
                        activeTasks.removeAll { it.id == task.id }
                        deferredTasks.add(0, task)
                    },
                )
                TodayLogCard(
                    completedCount = completedTasks.size,
                    deferredCount = deferredTasks.size + rescheduledTasks.size,
                )
                if (overdueTasks.none { it.recentlyExpired } && overdueTasks.isNotEmpty()) {
                    OverdueCard(
                        tasks = overdueTasks,
                        onComplete = { task ->
                            overdueTasks.removeAll { it.id == task.id }
                            completedTasks.add(0, TodayTask(id = task.id, title = task.title, priority = task.priority))
                        },
                        onReschedule = { task ->
                            overdueTasks.removeAll { it.id == task.id }
                            rescheduledTasks.add(0, task)
                        },
                        onDelete = { task ->
                            overdueTasks.removeAll { it.id == task.id }
                        },
                    )
                }
                WeekScheduleCard(today = today, events = events)
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun TodayBriefCard(
    today: LocalDate,
    dateLabel: String,
    completedCount: Int,
    activeCount: Int,
    progress: Float,
    focusTask: TodayTask?,
    urgentOverdueTask: OverdueTask?,
    onOpenAgent: () -> Unit,
) {
    var briefPage by remember { mutableIntStateOf(0) }

    LaunchedEffect(urgentOverdueTask?.id) {
        if (urgentOverdueTask == null) {
            briefPage = 0
            return@LaunchedEffect
        }
        while (true) {
            delay(3600)
            briefPage = (briefPage + 1) % 2
        }
    }

    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = AppleTextSecondary,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Today",
            style = MaterialTheme.typography.headlineSmall,
            color = AppleTextPrimary,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "先抓住最重要的事",
            style = MaterialTheme.typography.bodyMedium,
            color = AppleTextSecondary,
        )
        Spacer(Modifier.height(12.dp))
        BriefSpotlight(
            today = today,
            focusTask = focusTask,
            urgentOverdueTask = urgentOverdueTask,
            showOverdue = urgentOverdueTask != null && briefPage == 1,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricTile(
                label = "已完成",
                value = "$completedCount",
                modifier = Modifier.weight(1f),
            )
            MetricTile(
                label = "进行中",
                value = "$activeCount",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = AppleBlue,
            trackColor = AppleSurfaceDim,
        )
        Spacer(Modifier.height(10.dp))
        AgentBrief(onClick = onOpenAgent)
    }
}

@Composable
private fun PriorityTasksCard(
    tasks: List<TodayTask>,
    onCompleteTask: (TodayTask) -> Unit,
    onDeferTask: (TodayTask) -> Unit,
) {
    GlassCard {
        SectionHeader(title = "优先任务", caption = "右滑完成，左滑延后")
        Spacer(Modifier.height(14.dp))
        if (tasks.isEmpty()) {
            EmptyPriorityState()
        } else {
            tasks.forEachIndexed { index, task ->
                key(task.id) {
                    SwipeTaskRow(
                        task = task,
                        onComplete = { onCompleteTask(task) },
                        onDefer = { onDeferTask(task) },
                    )
                }
                if (index != tasks.lastIndex) {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun BriefSpotlight(
    today: LocalDate,
    focusTask: TodayTask?,
    urgentOverdueTask: OverdueTask?,
    showOverdue: Boolean,
) {
    AnimatedContent(
        targetState = showOverdue && urgentOverdueTask != null,
        transitionSpec = {
            slideInHorizontally(
                animationSpec = tween(durationMillis = 360),
                initialOffsetX = { width -> width },
            ) togetherWith slideOutHorizontally(
                animationSpec = tween(durationMillis = 360),
                targetOffsetX = { width -> -width },
            ) using SizeTransform(clip = true)
        },
        label = "brief_spotlight",
    ) { showingOverdue ->
        if (showingOverdue && urgentOverdueTask != null) {
            SpotlightCard(
                label = "刚过期",
                title = urgentOverdueTask.title,
                timeLabel = urgentOverdueTask.dueLabel,
                priority = urgentOverdueTask.priority,
                labelColor = AppleRed,
                backgroundColor = AppleRed.copy(alpha = 0.08f),
            )
        } else {
            SpotlightCard(
                label = "Focus",
                title = focusTask?.title ?: "当前没有需要处理的优先任务",
                timeLabel = focusTask?.let { "${it.time.displayText(today)} 优先处理" } ?: "可以回到日程页安排新的 Today 任务",
                priority = focusTask?.priority,
                labelColor = AppleTextSecondary,
                backgroundColor = AppleSurfaceDim.copy(alpha = 0.72f),
            )
        }
    }
}

@Composable
private fun SpotlightCard(
    label: String,
    title: String,
    timeLabel: String,
    priority: TaskPriority?,
    labelColor: Color,
    backgroundColor: Color,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp),
        shape = RoundedCornerShape(18.dp),
        color = backgroundColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = labelColor,
                    fontWeight = FontWeight.Medium,
                )
                if (priority != null) {
                    PriorityPill(priority = priority)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = AppleTextPrimary,
                fontWeight = FontWeight.Medium,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(0.dp))
            Text(
                text = timeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = AppleTextSecondary,
                minLines = 1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AgentBrief(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppleSurfaceDim.copy(alpha = 0.58f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatusDot(color = AppleBlue, size = 8.dp)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Agent Brief",
                style = MaterialTheme.typography.bodyMedium,
                color = AppleTextPrimary,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "上午处理 Focus，下午再清理延后和过期任务",
                style = MaterialTheme.typography.bodySmall,
                color = AppleTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = "调整",
            style = MaterialTheme.typography.bodySmall,
            color = AppleBlue,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun SwipeTaskRow(
    task: TodayTask,
    onComplete: () -> Unit,
    onDefer: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onComplete()
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDefer()
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { distance -> distance * 0.38f },
    )

    SwipeToDismissBox(
        modifier = Modifier.clip(RoundedCornerShape(18.dp)),
        state = dismissState,
        backgroundContent = {
            SwipeActionBackground(direction = dismissState.dismissDirection)
        },
    ) {
        TaskRow(task = task)
    }
}

@Composable
private fun SwipeActionBackground(direction: SwipeToDismissBoxValue) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Transparent),
    ) {
        SwipeActionBlock(
            text = "完成",
            color = AppleBlue,
            shape = RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp),
            modifier = Modifier.align(Alignment.CenterStart),
            isActive = direction == SwipeToDismissBoxValue.StartToEnd,
        )
        SwipeActionBlock(
            text = "延后",
            color = AppleTextSecondary,
            shape = RoundedCornerShape(topEnd = 18.dp, bottomEnd = 18.dp),
            modifier = Modifier.align(Alignment.CenterEnd),
            isActive = direction == SwipeToDismissBoxValue.EndToStart,
        )
    }
}

@Composable
private fun SwipeActionBlock(
    text: String,
    color: Color,
    shape: RoundedCornerShape,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(88.dp)
            .height(96.dp)
            .clip(shape)
            .background(color.copy(alpha = if (isActive) 0.16f else 0.1f))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun TaskRow(task: TodayTask) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            color = AppleTextPrimary,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = task.time.displayText(),
                style = MaterialTheme.typography.bodySmall,
                color = AppleTextSecondary,
            )
            PriorityPill(priority = task.priority)
        }
    }
}

@Composable
private fun TodayLogCard(
    completedCount: Int,
    deferredCount: Int,
) {
    GlassCard {
        SectionHeader(title = "Today Log", caption = "完成项进入复盘，延后项回到任务池")
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricTile(
                label = "复盘归档",
                value = "$completedCount",
                modifier = Modifier.weight(1f),
            )
            MetricTile(
                label = "待重排",
                value = "$deferredCount",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OverdueCard(
    tasks: List<OverdueTask>,
    onComplete: (OverdueTask) -> Unit,
    onReschedule: (OverdueTask) -> Unit,
    onDelete: (OverdueTask) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFFEFEF),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "过期提醒",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppleTextPrimary,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "有 ${tasks.size} 项需要处理",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppleTextSecondary,
                )
            }
            CountBadge(text = "${tasks.size}", color = AppleRed)
        }
        Spacer(Modifier.height(12.dp))
        tasks.forEachIndexed { index, task ->
            OverdueTaskRow(
                task = task,
                onComplete = { onComplete(task) },
                onReschedule = { onReschedule(task) },
                onDelete = { onDelete(task) },
            )
            if (index != tasks.lastIndex) {
                Spacer(Modifier.height(12.dp))
            }
        }
        }
    }
}

@Composable
private fun OverdueTaskRow(
    task: OverdueTask,
    onComplete: () -> Unit,
    onReschedule: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatusDot(color = task.priority.color)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppleTextPrimary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = task.dueLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppleTextSecondary,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ActionChip(text = "完成", color = AppleBlue, onClick = onComplete, modifier = Modifier.weight(1f))
                ActionChip(text = "重排", color = AppleTextSecondary, onClick = onReschedule, modifier = Modifier.weight(1f))
                ActionChip(text = "删除", color = AppleRed, onClick = onDelete, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WeekScheduleCard(
    today: LocalDate,
    events: List<TodayEvent>,
) {
    GlassCard {
        SectionHeader(title = "最近一周", caption = "从日程中同步过来的关键安排")
        Spacer(Modifier.height(10.dp))
        events.forEachIndexed { index, event ->
            EventRow(
                today = today,
                event = event,
                showLine = index != events.lastIndex,
            )
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = AppleSurfaceDim.copy(alpha = 0.7f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = AppleTextSecondary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = AppleTextPrimary,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ActionChip(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.1f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    caption: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = AppleTextPrimary,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = caption,
            style = MaterialTheme.typography.bodySmall,
            color = AppleTextSecondary,
        )
    }
}

@Composable
private fun EventRow(
    today: LocalDate,
    event: TodayEvent,
    showLine: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StatusDot(color = event.state.timelineColor(), size = 8.dp)
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(58.dp)
                        .background(AppleDivider),
                )
            }
        }
        Text(
            text = event.displayTime(today),
            style = MaterialTheme.typography.bodySmall,
            color = AppleTextSecondary,
            modifier = Modifier.width(72.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppleTextPrimary,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = event.note,
                style = MaterialTheme.typography.bodySmall,
                color = AppleTextSecondary,
            )
        }
    }
}

private fun TaskTimelineState.timelineColor(): Color = when (this) {
    TaskTimelineState.Overdue -> AppleRed
    TaskTimelineState.Completed -> AppleTextSecondary
    TaskTimelineState.InProgress -> AppleBlue
    TaskTimelineState.NotStarted,
    TaskTimelineState.Due -> AppleBlue
    TaskTimelineState.Unscheduled -> AppleTextSecondary
}

private fun sampleWeekEvents(now: LocalDateTime): List<TodayEvent> {
    val today = now.toLocalDate()
    val allTasks = listOf(
        TaskItem(
            id = "login-review",
            title = "复查登录入口",
            note = "收敛启动页交互",
            time = TaskTime(startAt = today.minusDays(2).atTime(16, 0), endAt = today.minusDays(2).atTime(17, 0)),
            status = TaskStatus.Completed,
            completedAt = today.minusDays(2).atTime(16, 45),
        ),
        TaskItem(
            id = "ui-feedback",
            title = "整理 UI 反馈",
            note = "合并 Today 页调整项",
            time = TaskTime(startAt = today.minusDays(1).atTime(20, 30)),
            status = TaskStatus.Completed,
            completedAt = today.minusDays(1).atTime(21, 10),
        ),
        TaskItem(
            id = "product-review",
            title = "产品走查",
            note = "确认 Today 信息层级",
            time = TaskTime(startAt = now.minusHours(2), endAt = now.plusMinutes(30)),
            status = TaskStatus.InProgress,
        ),
        TaskItem(
            id = "dev-window",
            title = "开发窗口",
            note = "预留连续时间处理任务闭环",
            time = TaskTime(startAt = now.plusHours(1), endAt = now.plusHours(3)),
            status = TaskStatus.Planned,
        ),
        TaskItem(
            id = "data-model",
            title = "数据模型截止",
            note = "整理开始/结束时间字段",
            time = TaskTime(endAt = today.plusDays(1).atTime(18, 0)),
            status = TaskStatus.Planned,
        ),
        TaskItem(
            id = "agent-review",
            title = "Agent 接口复查",
            note = "确认工具调用入口",
            time = TaskTime(startAt = today.plusDays(3).atTime(14, 0)),
            status = TaskStatus.Planned,
        ),
        TaskItem(
            id = "weekly-review",
            title = "周复盘",
            note = "沉淀完成项和延后项",
            time = TaskTime(startAt = today.plusDays(5).atTime(19, 30), endAt = today.plusDays(5).atTime(20, 0)),
            status = TaskStatus.Planned,
        ),
    )
    val allEvents = allTasks.mapNotNull { task -> task.toTodayEvent(now) }
    val activeEvents = allEvents
        .filter { it.state == TaskTimelineState.InProgress || it.state == TaskTimelineState.Overdue }
        .sortedBy { it.anchorAt }
        .take(6)
    val previousEvent = allEvents
        .filter { it.state == TaskTimelineState.Completed && it.anchorAt.isBefore(now) }
        .sortedBy { it.anchorAt }
        .takeLast(1)
    val remainingCount = (7 - activeEvents.size - previousEvent.size).coerceAtLeast(0)
    val upcomingEvents = allEvents
        .filter {
            (it.state == TaskTimelineState.NotStarted || it.state == TaskTimelineState.Due) &&
                !it.anchorAt.isBefore(now) &&
                !it.anchorAt.toLocalDate().isAfter(today.plusDays(6))
        }
        .sortedBy { it.anchorAt }
        .take(remainingCount)

    return activeEvents + previousEvent + upcomingEvents
}

private fun TodayEvent.displayTime(today: LocalDate): String {
    val date = anchorAt.toLocalDate()
    val dayLabel = when (date) {
        today.minusDays(2) -> "前天"
        today.minusDays(1) -> "昨天"
        today -> "今天"
        today.plusDays(1) -> "明天"
        else -> anchorAt.format(DateTimeFormatter.ofPattern("E", Locale.CHINA))
    }
    return "$dayLabel\n${time.displayText(date)} · ${state.label}"
}

private fun TaskItem.toTodayEvent(now: LocalDateTime): TodayEvent? {
    val anchor = timelineAnchor() ?: return null
    val state = timelineState(now)
    if (state == TaskTimelineState.Unscheduled) return null

    return TodayEvent(
        anchorAt = anchor,
        title = title,
        note = note ?: time.displayText(anchor.toLocalDate()),
        time = time,
        state = state,
    )
}

@Composable
private fun PriorityPill(priority: TaskPriority) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(priority.color.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StatusDot(color = priority.color, size = 6.dp)
        Text(
            text = priority.label,
            style = MaterialTheme.typography.bodySmall,
            color = priority.color,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun EmptyPriorityState() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = AppleSurfaceDim.copy(alpha = 0.62f),
    ) {
        Text(
            text = "当前没有待处理的优先任务",
            style = MaterialTheme.typography.bodyMedium,
            color = AppleTextSecondary,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun StatusDot(
    color: Color,
    size: androidx.compose.ui.unit.Dp = 8.dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun CountBadge(
    text: String,
    color: Color,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun Hairline() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AppleDivider),
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    FlawMateTheme { HomeScreen() }
}
