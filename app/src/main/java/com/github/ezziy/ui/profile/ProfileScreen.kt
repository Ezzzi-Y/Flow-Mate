package com.github.ezziy.ui.profile

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.ezziy.ui.components.AppleButtonSecondary
import com.github.ezziy.ui.components.AuroraBackground
import com.github.ezziy.ui.components.GlassCard
import com.github.ezziy.ui.theme.AppleBlue
import com.github.ezziy.ui.theme.AppleDivider
import com.github.ezziy.ui.theme.AppleSecondary
import com.github.ezziy.ui.theme.AppleSurfaceDim
import com.github.ezziy.ui.theme.AppleTextPrimary
import com.github.ezziy.ui.theme.AppleTextSecondary
import com.github.ezziy.ui.theme.FlawMateTheme

private data class ConnectionItem(
    val title: String,
    val subtitle: String,
    val state: ConnectionState,
)

private enum class ConnectionState(
    val label: String,
    val color: Color,
) {
    Connected("已连接", AppleBlue),
    Pending("待授权", AppleTextSecondary),
}

private enum class ProfileGlyph {
    Google,
    Calendar,
    Mail,
    Shield,
    Export,
    Info,
}

@Composable
fun ProfileScreen() {
    var smartBriefEnabled by remember { mutableStateOf(true) }
    var widgetEnabled by remember { mutableStateOf(false) }

    val connections = remember {
        listOf(
            ConnectionItem("Google 账号", "flowmate.user@gmail.com", ConnectionState.Connected),
            ConnectionItem("日历", "用于生成 Today 日程", ConnectionState.Pending),
            ConnectionItem("邮件", "用于提取待办和跟进事项", ConnectionState.Pending),
        )
    }

    AuroraBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Text(
                    text = "我的",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppleTextPrimary,
                    fontWeight = FontWeight.Medium,
                )

                AccountCard()

                ConnectionsCard(connections = connections)

                PreferencesCard(
                    smartBriefEnabled = smartBriefEnabled,
                    onSmartBriefChanged = { smartBriefEnabled = it },
                    widgetEnabled = widgetEnabled,
                    onWidgetChanged = { widgetEnabled = it },
                )

                DataCard()

                AboutCard()

                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun AccountCard() {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Avatar()
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = "FlowMate User",
                    style = MaterialTheme.typography.titleLarge,
                    color = AppleTextPrimary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "个人效率助理",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppleTextSecondary,
                )
            }
            StatusPill(text = "MVP", color = AppleBlue)
        }
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MetricTile(label = "Today 完成", value = "0", modifier = Modifier.weight(1f))
            MetricTile(label = "待重排", value = "1", modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        QuotaTile(
            label = "剩余额度",
            value = "82%",
            caption = "本月 Agent 调用额度",
            progress = 0.82f,
        )
        Spacer(Modifier.height(16.dp))
        AppleButtonSecondary(
            text = "管理账号",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ConnectionsCard(connections: List<ConnectionItem>) {
    GlassCard {
        SectionHeader(title = "工具连接", caption = "授权后用于生成 Today 和任务建议")
        Spacer(Modifier.height(10.dp))
        connections.forEachIndexed { index, item ->
            ConnectionRow(item = item)
            if (index != connections.lastIndex) {
                Hairline()
            }
        }
    }
}

@Composable
private fun PreferencesCard(
    smartBriefEnabled: Boolean,
    onSmartBriefChanged: (Boolean) -> Unit,
    widgetEnabled: Boolean,
    onWidgetChanged: (Boolean) -> Unit,
) {
    GlassCard {
        SectionHeader(title = "偏好", caption = "控制 FlowMate 的日常行为")
        Spacer(Modifier.height(10.dp))
        ToggleRow(
            title = "每日简报",
            subtitle = "每天早上生成 Today Focus",
            checked = smartBriefEnabled,
            onCheckedChange = onSmartBriefChanged,
        )
        Hairline()
        ToggleRow(
            title = "桌面小组件",
            subtitle = "展示当前优先任务",
            checked = widgetEnabled,
            onCheckedChange = onWidgetChanged,
        )
    }
}

@Composable
private fun DataCard() {
    GlassCard {
        SectionHeader(title = "数据与安全", caption = "本地优先，授权可控")
        Spacer(Modifier.height(10.dp))
        ActionRow(
            glyph = ProfileGlyph.Shield,
            title = "隐私与权限",
            subtitle = "查看账号、日历、邮件授权",
        )
        Hairline()
        ActionRow(
            glyph = ProfileGlyph.Export,
            title = "导出数据",
            subtitle = "任务、复盘和 Today Log",
        )
    }
}

@Composable
private fun AboutCard() {
    GlassCard {
        SectionHeader(title = "关于", caption = "FlowMate MVP")
        Spacer(Modifier.height(10.dp))
        ActionRow(
            glyph = ProfileGlyph.Info,
            title = "版本",
            subtitle = "1.0.0 debug",
            trailing = {
                StatusPill(text = "本地", color = AppleTextSecondary)
            },
        )
    }
}

@Composable
private fun ConnectionRow(item: ConnectionItem) {
    val glyph = when (item.title) {
        "Google 账号" -> ProfileGlyph.Google
        "日历" -> ProfileGlyph.Calendar
        else -> ProfileGlyph.Mail
    }

    ActionRow(
        glyph = glyph,
        title = item.title,
        subtitle = item.subtitle,
        trailing = {
            StatusPill(
                text = item.state.label,
                color = item.state.color,
                onClick = {},
            )
        },
    )
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppleTextPrimary,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppleTextSecondary,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppleBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AppleSecondary,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun ActionRow(
    glyph: ProfileGlyph,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val rowModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .then(rowModifier)
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        GlyphBubble(glyph = glyph)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = AppleTextPrimary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppleTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Text(
                text = ">",
                style = MaterialTheme.typography.bodyLarge,
                color = AppleTextSecondary,
            )
        }
    }
}

@Composable
private fun Avatar() {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(AppleBlue.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "FM",
            style = MaterialTheme.typography.titleLarge,
            color = AppleBlue,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun GlyphBubble(glyph: ProfileGlyph) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(AppleSurfaceDim.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center,
    ) {
        ProfileGlyphCanvas(glyph = glyph)
    }
}

@Composable
private fun ProfileGlyphCanvas(glyph: ProfileGlyph) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val strokeWidth = size.width * 0.09f
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        val color = AppleTextSecondary
        val w = size.width
        val h = size.height

        when (glyph) {
            ProfileGlyph.Google -> {
                drawCircle(color = color, radius = w * 0.32f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
                drawLine(color = color, start = Offset(w * 0.52f, h * 0.5f), end = Offset(w * 0.82f, h * 0.5f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            }
            ProfileGlyph.Calendar -> {
                drawRoundRect(color = color, topLeft = Offset(w * 0.18f, h * 0.22f), size = androidx.compose.ui.geometry.Size(w * 0.64f, h * 0.58f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.12f), style = stroke)
                drawLine(color = color, start = Offset(w * 0.3f, h * 0.42f), end = Offset(w * 0.7f, h * 0.42f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            }
            ProfileGlyph.Mail -> {
                drawRoundRect(color = color, topLeft = Offset(w * 0.16f, h * 0.26f), size = androidx.compose.ui.geometry.Size(w * 0.68f, h * 0.48f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.1f), style = stroke)
                drawLine(color = color, start = Offset(w * 0.2f, h * 0.32f), end = Offset(w * 0.5f, h * 0.54f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.8f, h * 0.32f), end = Offset(w * 0.5f, h * 0.54f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            }
            ProfileGlyph.Shield -> {
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.14f), end = Offset(w * 0.78f, h * 0.28f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.78f, h * 0.28f), end = Offset(w * 0.68f, h * 0.72f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.68f, h * 0.72f), end = Offset(w * 0.5f, h * 0.86f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.86f), end = Offset(w * 0.32f, h * 0.72f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.32f, h * 0.72f), end = Offset(w * 0.22f, h * 0.28f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.22f, h * 0.28f), end = Offset(w * 0.5f, h * 0.14f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            }
            ProfileGlyph.Export -> {
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.18f), end = Offset(w * 0.5f, h * 0.66f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.34f, h * 0.5f), end = Offset(w * 0.5f, h * 0.66f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.66f, h * 0.5f), end = Offset(w * 0.5f, h * 0.66f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.24f, h * 0.8f), end = Offset(w * 0.76f, h * 0.8f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            }
            ProfileGlyph.Info -> {
                drawCircle(color = color, radius = w * 0.34f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.46f), end = Offset(w * 0.5f, h * 0.66f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawCircle(color = color, radius = strokeWidth * 0.7f, center = Offset(w * 0.5f, h * 0.32f))
            }
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(AppleSurfaceDim.copy(alpha = 0.72f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = AppleTextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = AppleTextPrimary,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun QuotaTile(
    label: String,
    value: String,
    caption: String,
    progress: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppleSurfaceDim.copy(alpha = 0.72f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleTextSecondary,
                )
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppleTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = AppleTextPrimary,
                fontWeight = FontWeight.Medium,
            )
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = AppleBlue,
            trackColor = AppleSecondary,
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
private fun StatusPill(
    text: String,
    color: Color,
    onClick: (() -> Unit)? = null,
) {
    val pillModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.1f))
            .then(pillModifier)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
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
private fun ProfileScreenPreview() {
    FlawMateTheme { ProfileScreen() }
}
