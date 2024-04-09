package com.example.moneymindermobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneymindermobile.data.HttpClient
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.ui.screens.HomeScreen
import com.example.moneymindermobile.ui.screens.LoginScreen
import com.example.moneymindermobile.ui.screens.RegistrationScreen
import com.example.moneymindermobile.ui.theme.MoneyMinderMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MainViewModel(httpClient = HttpClient)
        setContent {
            MoneyMinderMobileTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = routes.LOGIN) {
                        composable(routes.HOME) {
                            HomeScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(routes.LOGIN) {
                            LoginScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(routes.REGISTER) {
                            RegistrationScreen(viewModel = viewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
