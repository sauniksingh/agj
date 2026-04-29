package athato.ghummakd.jigayasa.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import athato.ghummakd.jigayasa.presentation.add.AddEventScreen
import athato.ghummakd.jigayasa.presentation.list.EventListScreen

object Routes {
    const val LIST = "list"
    const val ADD = "event/add"
    const val EDIT_PATTERN = "event/edit/{id}"
    fun edit(id: Int) = "event/edit/$id"
    const val EDIT_ARG_ID = "id"
}

@Composable
fun AgjNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.LIST,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            ) + fadeIn(tween(300))
        },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(200)) },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            ) + fadeOut(tween(300))
        }
    ) {
        composable(Routes.LIST) {
            EventListScreen(
                onNavigateToAdd = { navController.navigate(Routes.ADD) },
                onNavigateToEdit = { id -> navController.navigate(Routes.edit(id)) }
            )
        }
        composable(Routes.ADD) {
            AddEventScreen(onClose = { navController.popBackStack() })
        }
        composable(
            route = Routes.EDIT_PATTERN,
            arguments = listOf(navArgument(Routes.EDIT_ARG_ID) { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt(Routes.EDIT_ARG_ID)
            AddEventScreen(
                onClose = { navController.popBackStack() },
                editingId = id
            )
        }
    }
}
