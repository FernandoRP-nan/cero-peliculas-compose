package com.example.cero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cero.feature.splash.SplashScreen
import com.example.cero.feature.splash.SplashViewModel
import com.example.cero.feature.wallet.WalletRoute
import com.example.cero.ui.theme.CeroTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CeroTheme {
                AppEntry()
            }
        }
    }
}

@Composable
private fun AppEntry(
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by splashViewModel.uiState.collectAsState()

    if (uiState.isReady) {
        WalletRoute(performanceMode = uiState.performanceMode)
    } else {
        SplashScreen(performanceMode = uiState.performanceMode)
    }
}
