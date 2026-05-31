package com.github.ezziy.ui.schedule

import android.icu.util.Calendar as IcuCalendar
import android.icu.util.ChineseCalendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.ezziy.model.TaskItem
import com.github.ezziy.model.TaskPriority
import com.github.ezziy.model.TaskStatus
import com.github.ezziy.model.TaskTime
import com.github.ezziy.ui.components.AuroraBackground
import com.github.ezziy.ui.theme.AppleBlue
import com.github.ezziy.ui.theme.AppleRed
import com.github.ezziy.ui.theme.AppleSurfaceDim
import com.github.ezziy.ui.theme.AppleTextPrimary
import com.github.ezziy.ui.theme.AppleTextSecondary
import com.github.ezziy.ui.theme.FlawMateTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.GregorianCalendar
import java.util.Locale

@Composable
fun ScheduleScreen() {
    val today = remember { LocalDate.now() }
    val month = remember(today) { YearMonth.from(today) }
    val tasks = remember(today) { sampleCalendarTasks(today) }

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
                    .padding(horizontal = 24.dp, vertical = 26.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                ScheduleHeader(month = month)
                MonthCalendarStream(
                    month = month,
                    today = today,
                    tasks = tasks,
                )
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun ScheduleHeader(month: YearMonth) {
    val monthText = month.format(DateTimeFormatter.ofPattern("yyyy年 M月", Locale.CHINA))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "日程",
                style = MaterialTheme.typography.headlineSmall,
                color = AppleTextPrimary,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "把任务放进时间里",
                style = MaterialTheme.typography.bodyMedium,
                color = AppleTextSecondary,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White)
                .padding(horizontal = 13.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = monthText,
                style = MaterialTheme.typography.bodySmall,
                color = AppleTextPrimary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MonthCalendarStream(
    month: YearMonth,
    today: LocalDate,
    tasks: List<TaskItem>,
) {
    Column {
        val monthOffsets = if (today.dayOfMonth <= 10) -1..2 else 0..2
        monthOffsets.forEach { offset ->
            val streamMonth = month.plusMonths(offset.toLong())
            if (streamMonth != month) {
                Text(
                    text = streamMonth.format(DateTimeFormatter.ofPattern("yyyy年 M月", Locale.CHINA)),
                    style = MaterialTheme.typography.titleMedium,
                    color = AppleTextPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                )
            }
            MonthGrid(
                month = streamMonth,
                today = today,
                tasks = tasks,
            )
        }
    }
}

@Composable
private fun MonthGrid(
    month: YearMonth,
    today: LocalDate,
    tasks: List<TaskItem>,
) {
    val days = remember(month) { monthCells(month) }

    WeekdayHeader()
    Spacer(Modifier.height(14.dp))
    days.chunked(7).forEach { week ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            week.forEach { date ->
                CalendarDayCell(
                    date = date,
                    currentMonth = month,
                    today = today,
                    tasks = tasks.filter { it.time.occursOn(date) }.take(3),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun WeekdayHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        listOf("日", "一", "二", "三", "四", "五", "六").forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = AppleTextSecondary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    currentMonth: YearMonth,
    today: LocalDate,
    tasks: List<TaskItem>,
    modifier: Modifier = Modifier,
) {
    val isToday = date == today
    val inCurrentMonth = YearMonth.from(date) == currentMonth
    val backgroundColor = if (isToday) Color.White else Color.Transparent

    Column(
        modifier = modifier
            .aspectRatio(0.52f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 5.dp, vertical = 7.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(
            text = if (date.dayOfMonth == 1 && !inCurrentMonth) "${date.monthValue}/${date.dayOfMonth}" else date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = when {
                isToday -> AppleTextPrimary
                inCurrentMonth -> AppleTextSecondary
                else -> AppleTextSecondary.copy(alpha = 0.58f)
            },
            fontWeight = if (isToday) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
        )
        Text(
            text = lunarPlaceholder(date),
            style = MaterialTheme.typography.labelSmall,
            color = AppleTextSecondary.copy(alpha = if (inCurrentMonth) 0.78f else 0.46f),
            maxLines = 1,
        )
        tasks.forEach { task ->
            Text(
                text = task.title,
                style = MaterialTheme.typography.labelSmall,
                color = priorityColor(task.priority),
                fontSize = 9.sp,
                lineHeight = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (isToday) {
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(AppleBlue),
            )
        }
    }
}

private fun monthCells(month: YearMonth): List<LocalDate> {
    val firstDay = month.atDay(1)
    val leadingEmptyDays = firstDay.dayOfWeek.value % 7
    val cellCount = ((leadingEmptyDays + month.lengthOfMonth() + 6) / 7) * 7

    return (0 until cellCount).map { index ->
        val day = index - leadingEmptyDays + 1
        firstDay.plusDays((day - 1).toLong())
    }
}

private fun lunarPlaceholder(date: LocalDate): String {
    val chineseCalendar = ChineseCalendar()
    chineseCalendar.time = GregorianCalendar(date.year, date.monthValue - 1, date.dayOfMonth).time
    val lunarDay = chineseCalendar.get(IcuCalendar.DAY_OF_MONTH)
    return lunarDayLabel(lunarDay)
}

private fun lunarDayLabel(day: Int): String = when (day) {
    1 -> "初一"
    2 -> "初二"
    3 -> "初三"
    4 -> "初四"
    5 -> "初五"
    6 -> "初六"
    7 -> "初七"
    8 -> "初八"
    9 -> "初九"
    10 -> "初十"
    11 -> "十一"
    12 -> "十二"
    13 -> "十三"
    14 -> "十四"
    15 -> "十五"
    16 -> "十六"
    17 -> "十七"
    18 -> "十八"
    19 -> "十九"
    20 -> "二十"
    21 -> "廿一"
    22 -> "廿二"
    23 -> "廿三"
    24 -> "廿四"
    25 -> "廿五"
    26 -> "廿六"
    27 -> "廿七"
    28 -> "廿八"
    29 -> "廿九"
    30 -> "三十"
    else -> " "
}

private fun sampleCalendarTasks(today: LocalDate): List<TaskItem> = listOf(
    TaskItem(
        id = "today-ui",
        title = "Today 页面",
        time = TaskTime(startAt = today.atTime(10, 30), endAt = today.atTime(12, 0)),
        priority = TaskPriority.High,
        status = TaskStatus.Planned,
    ),
    TaskItem(
        id = "data-model",
        title = "数据模型",
        time = TaskTime(endAt = today.plusDays(1).atTime(18, 0)),
        priority = TaskPriority.Medium,
        status = TaskStatus.Planned,
    ),
    TaskItem(
        id = "agent-entry",
        title = "Agent 接口",
        time = TaskTime(startAt = today.plusDays(2).atTime(14, 0)),
        priority = TaskPriority.Low,
        status = TaskStatus.InProgress,
    ),
    TaskItem(
        id = "review",
        title = "复盘",
        time = TaskTime(startAt = today.plusDays(4).atTime(19, 30), endAt = today.plusDays(4).atTime(20, 0)),
        priority = TaskPriority.Medium,
        status = TaskStatus.Planned,
    ),
)

private fun priorityColor(priority: TaskPriority): Color = when (priority) {
    TaskPriority.High -> AppleRed
    TaskPriority.Medium -> AppleBlue
    TaskPriority.Low -> AppleTextSecondary
}

@Preview(showBackground = true)
@Composable
private fun ScheduleScreenPreview() {
    FlawMateTheme { ScheduleScreen() }
}
