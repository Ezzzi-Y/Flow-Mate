package com.github.ezziy.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.ezziy.ui.theme.AppleBlue
import com.github.ezziy.ui.theme.AppleSecondary
import com.github.ezziy.ui.theme.AppleTextPrimary

private val CapsuleShape = RoundedCornerShape(999.dp)

@Composable
fun AppleButtonPrimary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = CapsuleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppleBlue,
            contentColor = Color.White,
            disabledContainerColor = AppleBlue.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.6f),
        ),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Text(text)
    }
}

@Composable
fun AppleButtonSecondary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = CapsuleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppleSecondary,
            contentColor = AppleTextPrimary,
            disabledContainerColor = AppleSecondary.copy(alpha = 0.4f),
            disabledContentColor = AppleTextPrimary.copy(alpha = 0.4f),
        ),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
    ) {
        Text(text)
    }
}
