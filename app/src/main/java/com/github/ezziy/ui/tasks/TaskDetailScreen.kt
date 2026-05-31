package com.github.ezziy.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.ezziy.ui.components.AppleButtonPrimary
import com.github.ezziy.ui.components.AppleButtonSecondary
import com.github.ezziy.ui.components.AuroraBackground
import com.github.ezziy.ui.theme.AppleTextSecondary
import com.github.ezziy.ui.theme.FlawMateTheme

@Composable
fun TaskDetailScreen(
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
) {
    var title by remember { mutableStateOf("") }

    AuroraBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = "新建任务",
                style = MaterialTheme.typography.headlineLarge,
            )

            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("任务标题", color = AppleTextSecondary) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF2F2F7),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            // 截止时间占位
            Text(
                text = "截止时间：待实现",
                style = MaterialTheme.typography.bodyMedium,
                color = AppleTextSecondary,
            )

            // 优先级占位
            Text(
                text = "优先级：待实现",
                style = MaterialTheme.typography.bodyMedium,
                color = AppleTextSecondary,
            )

            Spacer(Modifier.height(16.dp))

            AppleButtonPrimary(
                text = "保存",
                onClick = onSaved,
                modifier = Modifier.fillMaxWidth(),
            )
            AppleButtonSecondary(
                text = "取消",
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskDetailScreenPreview() {
    FlawMateTheme { TaskDetailScreen() }
}
