package com.github.ezziy.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.ezziy.ui.components.AuroraBackground
import com.github.ezziy.ui.components.GlassCard
import com.github.ezziy.ui.theme.AppleTextSecondary
import com.github.ezziy.ui.theme.FlawMateTheme

@Composable
fun TasksScreen(
    onTaskClick: (String) -> Unit = {},
    onCreateTask: () -> Unit = {},
) {
    AuroraBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Text(
                    text = "任务",
                    style = MaterialTheme.typography.headlineLarge,
                )

                GlassCard {
                    Text(
                        text = "任务列表占位",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppleTextSecondary,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TasksScreenPreview() {
    FlawMateTheme { TasksScreen() }
}
