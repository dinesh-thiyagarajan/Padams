package com.app.padams.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.padams.presentation.screens.albums.AlbumDetailScreen
import com.app.padams.presentation.screens.albums.AlbumsScreen
import com.app.padams.presentation.screens.detail.ImageDetailScreen
import com.app.padams.presentation.screens.favorites.FavoritesScreen
import com.app.padams.presentation.screens.people.PeopleScreen
import com.app.padams.presentation.screens.people.PersonDetailScreen
import com.app.padams.presentation.screens.permission.PermissionScreen
import com.app.padams.presentation.screens.photos.PhotosScreen

@Composable
fun PadamsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Permission.route,
        modifier = modifier
    ) {
        composable(Screen.Permission.route) {
            PermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Screen.Photos.route) {
                        popUpTo(Screen.Permission.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Photos.route) {
            PhotosScreen(
                onPhotoClick = { uri ->
                    navController.navigate(Screen.ImageDetail.createRoute(uri))
                }
            )
        }

        composable(Screen.Albums.route) {
            AlbumsScreen(
                onAlbumClick = { albumId ->
                    navController.navigate(Screen.AlbumDetail.createRoute(albumId))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onPhotoClick = { uri ->
                    navController.navigate(Screen.ImageDetail.createRoute(uri))
                }
            )
        }

        composable(Screen.People.route) {
            PeopleScreen(
                onPersonClick = { personId ->
                    navController.navigate(Screen.PersonDetail.createRoute(personId))
                }
            )
        }

        composable(
            route = Screen.AlbumDetail.route,
            arguments = listOf(navArgument("albumId") { type = NavType.LongType })
        ) {
            AlbumDetailScreen(
                onPhotoClick = { uri ->
                    navController.navigate(Screen.ImageDetail.createRoute(uri))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PersonDetail.route,
            arguments = listOf(navArgument("personId") { type = NavType.LongType })
        ) {
            PersonDetailScreen(
                onPhotoClick = { uri ->
                    navController.navigate(Screen.ImageDetail.createRoute(uri))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ImageDetail.route,
            arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
        ) {
            ImageDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
