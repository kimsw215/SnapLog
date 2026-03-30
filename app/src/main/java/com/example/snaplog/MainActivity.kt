package com.example.snaplog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.snaplog.presentation.navigation.SnapLogNavGraph
import com.example.snaplog.ui.theme.SnapLogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnapLogTheme {
                Surface {
                    val navController = rememberNavController()
                    SnapLogNavGraph(navController = navController)
                }
            }
        }
    }
}