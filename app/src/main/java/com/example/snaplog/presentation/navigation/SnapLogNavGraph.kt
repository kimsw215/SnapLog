package com.example.snaplog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.snaplog.presentation.camera.CameraScreen
import com.example.snaplog.presentation.detail.PhotoDetailScreen
import com.example.snaplog.presentation.home.HomeScreen
import com.example.snaplog.presentation.save.SavePhotoScreen
import com.example.snaplog.presentation.update.PhotoUpdateScreen

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
                onAddClick = { navController.navigate(NavRoutes.SAVE_PHOTO) },
                onPhotoClick = { id ->
                    navController.navigate("${NavRoutes.DETAIL}/$id")
                }
            )
        }
        composable(NavRoutes.SAVE_PHOTO) {
            val savedStateHandle = it.savedStateHandle
            SavePhotoScreen(
                savedStateHandle = savedStateHandle,
                onNavigateToCamera = { navController.navigate(NavRoutes.CAMERA) },
                onSave = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("${NavRoutes.DETAIL}/{photoId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("photoId")?.toLongOrNull() ?: -1L
            PhotoDetailScreen(
                photoId = id,
                onUpdate = { photoId ->
                    navController.navigate("${NavRoutes.UPDATE}/$photoId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("${NavRoutes.UPDATE}/{photoId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("photoId")?.toLongOrNull() ?: -1L
            val savedStateHandle = backStackEntry.savedStateHandle
            PhotoUpdateScreen(
                photoId = id,
                savedStateHandle = savedStateHandle,
                onNavigateToCamera = { navController.navigate(NavRoutes.CAMERA) },
                onUpdate = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                },
                onDelete = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.CAMERA) {
            CameraScreen(
                onCaptured = { imagePath ->
                    val prevEntry = navController.previousBackStackEntry
                    prevEntry?.savedStateHandle?.set("imagePath", imagePath)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}