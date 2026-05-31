package com.github.ezziy.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.ezziy.ui.components.AppleButtonPrimary
import com.github.ezziy.ui.components.AuroraBackground
import com.github.ezziy.ui.components.GoogleIcon
import com.github.ezziy.ui.theme.AppleTextSecondary
import com.github.ezziy.ui.theme.FlawMateTheme

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}) {
    AuroraBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.size(80.dp))
            Text(
                text = "FlowMate",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF1D1D1F),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "让每一天都有迹可循",
                style = MaterialTheme.typography.bodyMedium,
                color = AppleTextSecondary,
            )

            Spacer(Modifier.height(48.dp))

            AppleButtonPrimary(
                text = "使用 Google 登录",
                onClick = onLoginSuccess,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { GoogleIcon() },
            )

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onLoginSuccess) {
                Text(
                    text = "稍后体验",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppleTextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    FlawMateTheme { LoginScreen() }
}
