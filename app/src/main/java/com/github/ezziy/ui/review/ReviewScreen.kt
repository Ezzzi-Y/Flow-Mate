package com.github.ezziy.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.ezziy.ui.components.AuroraBackground
import com.github.ezziy.ui.components.GlassCard
import com.github.ezziy.ui.theme.AppleTextSecondary
import com.github.ezziy.ui.theme.FlawMateTheme

@Composable
fun ReviewScreen(onBack: () -> Unit = {}) {
    AuroraBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = "复盘 / 统计",
                style = MaterialTheme.typography.headlineLarge,
            )
            GlassCard {
                Text(
                    text = "统计数据占位",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppleTextSecondary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewScreenPreview() {
    FlawMateTheme { ReviewScreen() }
}
