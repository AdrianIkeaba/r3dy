package com.ghostdev.r3ady.navigation

sealed class NavDestinations(val route: String) {

    data object Home: NavDestinations("home")
    data object DemoViewer: NavDestinations("demo")
    data object CustomViewer: NavDestinations("custom")

}