package com.example.movierecommendation.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movierecommendation.ui.screens.*
import com.example.movierecommendation.viewmodel.MoviesViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Movies : Screen("movies", "Movies", Icons.Filled.Movie, Icons.Outlined.Movie)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object Watchlist : Screen("watchlist", "Watchlist", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    object MovieDetail : Screen("movie_detail/{movieId}", "Detail", Icons.Filled.Movie, Icons.Outlined.Movie) {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
    object Splash : Screen("splash", "Splash", Icons.Filled.Movie, Icons.Outlined.Movie)
}

val bottomNavItems = listOf(Screen.Movies, Screen.Favorites, Screen.Watchlist)

@Composable
fun AppNavHost(viewModel: MoviesViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Check if we should show bottom nav
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashComplete = {
                        navController.navigate(Screen.Movies.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Movies.route) {
                MoviesScreen(
                    viewModel = viewModel,
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    }
                )
            }

            composable(Screen.Watchlist.route) {
                WatchlistScreen(
                    viewModel = viewModel,
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    }
                )
            }

            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                MovieDetailScreen(
                    movieId = movieId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
