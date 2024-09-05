package com.example.newsshorts.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.newsshorts.presentation.articleScreen.ArticleScreen
import com.example.newsshorts.presentation.newsScreen.NewsScreen
import com.example.newsshorts.presentation.newsScreen.NewsViewModel

@Composable
fun AppNavigation(viewModel: NewsViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "news_screen"
    ) {
        composable("news_screen") {
            NewsScreen(
                viewModel,
                onEvent = viewModel::onEvent,
                onReadFullStoryButtonClicked = { url ->
                    navController.navigate("article_screen?web_url=$url")
                }
            )
        }
        composable(
            "article_screen?web_url={web_url}",
            arguments = listOf(navArgument("web_url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("web_url")
            ArticleScreen(url = url, onBackPressed = { navController.popBackStack() })
        }

    }
}