package com.example.snaplog.presentation.util

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionState(
    val permission: String,
    private val activity: ComponentActivity
) {
    var hasPermission by mutableStateOf(
        ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    )
        private set

    fun launchPermissionRequest() {
        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                100
            )
        }
    }

    fun onPermissionResult(granted: Boolean) {
        hasPermission = granted
    }
}

@Composable
fun rememberPermissionState(permission: String): PermissionState {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    return remember {
        PermissionState(permission,activity)
    }
}