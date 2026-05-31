package com.github.ezziy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.ezziy.navigation.AppNavGraph
import com.github.ezziy.ui.theme.FlawMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlawMateTheme {
                AppNavGraph()
            }
        }
    }
}
