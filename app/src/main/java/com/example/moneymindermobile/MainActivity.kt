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
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.data.api.ApiEndpoints
import com.example.moneymindermobile.ui.screens.HomeScreen
import com.example.moneymindermobile.ui.screens.LoginScreen
import com.example.moneymindermobile.ui.screens.RegistrationScreen
import com.example.moneymindermobile.ui.theme.MoneyMinderMobileTheme
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import java.net.CookiePolicy

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        val okHttpClient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()

        val apolloClient = ApolloClient.Builder()
            .serverUrl(ApiEndpoints.GRAPHQL)
            .okHttpClient(okHttpClient)
            .build()

        val viewModel = MainViewModel(apolloClient)
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
