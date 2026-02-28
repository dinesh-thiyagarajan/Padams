package com.app.padams.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Photos : Screen("photos")
    data object Albums : Screen("albums")
    data object Favorites : Screen("favorites")
    data object People : Screen("people")

    data object AlbumDetail : Screen("album/{albumId}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }

    data object PersonDetail : Screen("person/{personId}") {
        fun createRoute(personId: Long) = "person/$personId"
    }

    data object ImageDetail : Screen("image/{imageUri}") {
        fun createRoute(imageUri: String) = "image/${Uri.encode(imageUri)}"
    }

    data object Permission : Screen("permission")
}
