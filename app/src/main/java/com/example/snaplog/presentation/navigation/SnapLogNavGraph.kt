package com.example.snaplog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.snaplog.presentation.camera.CameraScreen
import com.example.snaplog.presentation.capture.CaptureReviewScreen
import com.example.snaplog.presentation.detail.PhotoDetailScreen
import com.example.snaplog.presentation.home.HomeScreen

@Composable
fun SnapLogNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onAddClick = { navController.navigate(NavRoutes.CAMERA) },
                onPhotoClick = { id ->
                    navController.navigate("${NavRoutes.DETAIL}/$id")
                }
            )
        }
        composable(NavRoutes.CAMERA) {
            CameraScreen(
                onCaptured = { imagePath ->
                    navController.navigate("${NavRoutes.CAPTURE_REVIEW}/$imagePath") {
                        popUpTo(NavRoutes.CAMERA) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("${NavRoutes.CAPTURE_REVIEW}/{imagePath}") { backStackEntry ->
            val imagePath = backStackEntry.arguments?.getString("imagePath") ?: ""
            CaptureReviewScreen(
                imagePath = imagePath,
                onSaved = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("${NavRoutes.DETAIL}/{photoId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("photoId")?.toLongOrNull() ?: -1L
            PhotoDetailScreen(
                photoId = id,
                onBack = { navController.popBackStack() }
            )
        }

    }
}