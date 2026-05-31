package com.github.ezziy.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.ezziy.ui.agent.AgentScreen
import com.github.ezziy.ui.home.HomeScreen
import com.github.ezziy.ui.login.LoginScreen
import com.github.ezziy.ui.profile.ProfileScreen
import com.github.ezziy.ui.review.ReviewScreen
import com.github.ezziy.ui.schedule.ScheduleScreen
import com.github.ezziy.ui.tasks.TaskDetailScreen
import com.github.ezziy.ui.theme.AppleBackground
import com.github.ezziy.ui.theme.AppleBlue
import com.github.ezziy.ui.theme.AppleDivider
import com.github.ezziy.ui.theme.AppleSurface
import com.github.ezziy.ui.theme.AppleTextSecondary

// ── 路由常量 ─────────────────────────────────────────────
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val SCHEDULE = "schedule"
    const val AGENT = "agent"
    const val PROFILE = "profile"
    const val TASK_DETAIL = "task_detail?taskId={taskId}"
    const val REVIEW = "review"

    fun taskDetail(taskId: String? = null) =
        if (taskId != null) "task_detail?taskId=$taskId" else "task_detail"
}

// ── 底部导航项 ─────────────────────────────────────────
data class BottomNavItem(
    val route: String,
    val label: String,
    val glyph: BottomNavGlyph,
)

enum class BottomNavGlyph {
    Today,
    Schedule,
    Agent,
    Profile,
}

private val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Today", BottomNavGlyph.Today),
    BottomNavItem(Routes.SCHEDULE, "日程", BottomNavGlyph.Schedule),
    BottomNavItem(Routes.AGENT, "Agent", BottomNavGlyph.Agent),
    BottomNavItem(Routes.PROFILE, "我的", BottomNavGlyph.Profile),
)

// ── 入口 ─────────────────────────────────────────────
@Composable
fun AppNavGraph(startRoute: String = Routes.LOGIN) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        containerColor = AppleBackground,
        bottomBar = {
            if (showBottomBar) {
                AppleBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenAgent = { navController.navigate(Routes.AGENT) },
                )
            }
            composable(Routes.SCHEDULE) {
                ScheduleScreen()
            }
            composable(
                Routes.TASK_DETAIL,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType; nullable = true }),
            ) {
                TaskDetailScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
            composable(Routes.AGENT) { AgentScreen() }
            composable(Routes.REVIEW) {
                ReviewScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.PROFILE) { ProfileScreen() }
        }
    }
}

@Composable
private fun AppleBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (BottomNavItem) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = AppleSurface.copy(alpha = 0.9f),
        contentColor = AppleTextSecondary,
        tonalElevation = 0.dp,
        shadowElevation = 10.dp,
        border = BorderStroke(0.5.dp, AppleDivider),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                AppleBottomBarItem(
                    item = item,
                    selected = selected,
                    onClick = { onNavigate(item) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AppleBottomBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemColor = if (selected) AppleBlue else AppleTextSecondary
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AppleNavGlyph(
            glyph = item.glyph,
            color = itemColor,
            size = 22.dp,
        )
        Text(
            text = item.label,
            color = itemColor,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
        )
    }
}

@Composable
private fun AppleNavGlyph(
    glyph: BottomNavGlyph,
    color: androidx.compose.ui.graphics.Color,
    size: Dp,
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = w * 0.08f
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

        when (glyph) {
            BottomNavGlyph.Today -> {
                val center = Offset(w * 0.5f, h * 0.5f)
                drawCircle(color = color, radius = w * 0.2f, center = center, style = stroke)
                val rays = listOf(
                    Offset(0.5f, 0.08f) to Offset(0.5f, 0.2f),
                    Offset(0.5f, 0.8f) to Offset(0.5f, 0.92f),
                    Offset(0.08f, 0.5f) to Offset(0.2f, 0.5f),
                    Offset(0.8f, 0.5f) to Offset(0.92f, 0.5f),
                    Offset(0.2f, 0.2f) to Offset(0.29f, 0.29f),
                    Offset(0.71f, 0.71f) to Offset(0.8f, 0.8f),
                    Offset(0.8f, 0.2f) to Offset(0.71f, 0.29f),
                    Offset(0.29f, 0.71f) to Offset(0.2f, 0.8f),
                )
                rays.forEach { (start, end) ->
                    drawLine(
                        color = color,
                        start = Offset(w * start.x, h * start.y),
                        end = Offset(w * end.x, h * end.y),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round,
                    )
                }
            }
            BottomNavGlyph.Schedule -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.18f, h * 0.2f),
                    size = Size(w * 0.64f, h * 0.62f),
                    cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
                    style = stroke,
                )
                drawLine(
                    color = color,
                    start = Offset(w * 0.18f, h * 0.36f),
                    end = Offset(w * 0.82f, h * 0.36f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(w * 0.34f, h * 0.12f),
                    end = Offset(w * 0.34f, h * 0.26f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(w * 0.66f, h * 0.12f),
                    end = Offset(w * 0.66f, h * 0.26f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }
            BottomNavGlyph.Agent -> {
                drawLine(
                    color = color,
                    start = Offset(w * 0.5f, h * 0.16f),
                    end = Offset(w * 0.5f, h * 0.72f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(w * 0.22f, h * 0.44f),
                    end = Offset(w * 0.78f, h * 0.44f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(w * 0.68f, h * 0.68f),
                    end = Offset(w * 0.82f, h * 0.82f),
                    strokeWidth = strokeWidth * 0.8f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(w * 0.82f, h * 0.68f),
                    end = Offset(w * 0.68f, h * 0.82f),
                    strokeWidth = strokeWidth * 0.8f,
                    cap = StrokeCap.Round,
                )
            }
            BottomNavGlyph.Profile -> {
                drawCircle(
                    color = color,
                    radius = w * 0.16f,
                    center = Offset(w * 0.5f, h * 0.34f),
                    style = stroke,
                )
                drawArc(
                    color = color,
                    startAngle = 205f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = Offset(w * 0.24f, h * 0.52f),
                    size = Size(w * 0.52f, h * 0.42f),
                    style = stroke,
                )
            }
        }
    }
}
