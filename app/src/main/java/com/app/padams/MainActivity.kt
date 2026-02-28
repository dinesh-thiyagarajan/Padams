package com.app.padams

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.app.padams.presentation.ads.BannerAdView
import com.app.padams.presentation.ads.InterstitialAdManager
import com.app.padams.presentation.navigation.BottomNavBar
import com.app.padams.presentation.navigation.PadamsNavHost
import com.app.padams.ui.theme.PadamsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Preload interstitial ad
        InterstitialAdManager.load(this)

        setContent {
            PadamsTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        Column {
                            BannerAdView(modifier = Modifier.fillMaxWidth())
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    PadamsNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
