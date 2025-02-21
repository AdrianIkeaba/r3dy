package com.ghostdev.r3ady.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ghostdev.r3ady.ui.presentation.CustomSceneScreen
import com.ghostdev.r3ady.ui.presentation.DemoScreen
import com.ghostdev.r3ady.ui.presentation.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavDestinations.Home.toString()) {
        composable(NavDestinations.Home.toString()) { HomeScreen(navController) }

        composable(NavDestinations.DemoViewer.toString()) { DemoScreen(navController) }

        composable("${NavDestinations.CustomViewer}/{uri}") { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")?.let { Uri.parse(it) }
            CustomSceneScreen(navController, uri)
        }
    }
}