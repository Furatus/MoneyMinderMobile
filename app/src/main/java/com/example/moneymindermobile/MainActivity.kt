package com.example.moneymindermobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import com.example.moneymindermobile.ui.screens.ExpenseDetailScreen
import com.example.moneymindermobile.ui.screens.GroupDetailsScreen
import com.example.moneymindermobile.ui.screens.GroupStatsDetailScreen
import com.example.moneymindermobile.ui.screens.HomeScreen
import com.example.moneymindermobile.ui.screens.LoginScreen
import com.example.moneymindermobile.ui.screens.RegistrationScreen
import com.example.moneymindermobile.ui.screens.StatsScreen
import com.example.moneymindermobile.ui.screens.UserDetailsScreen
import com.example.moneymindermobile.ui.screens.UserPaymentScreen
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

        val viewModel = MainViewModel(apolloClient, okHttpClient)
        setContent {
            MoneyMinderMobileTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Routes.LOGIN) {
                        composable(Routes.HOME) {
                            viewModel.refreshGraphQlError()
                            BackHandler(true) {

                            }
                            HomeScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(Routes.LOGIN) {
                            viewModel.refreshGraphQlError()
                            BackHandler(true) {

                            }
                            LoginScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(Routes.REGISTER) {
                            viewModel.refreshGraphQlError()
                            RegistrationScreen(viewModel = viewModel, navController = navController)
                        }
                        composable("${Routes.GROUP_DETAILS}/{groupId}") { navBackStackEntry ->
                            viewModel.refreshGraphQlError()
                            val groupId =
                                navBackStackEntry.arguments?.getString("groupId")
                            GroupDetailsScreen(
                                groupId = groupId,
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                        composable("${Routes.EXPENSE_DETAILS}/{expenseId}") { navBackStackEntry ->
                            viewModel.refreshGraphQlError()
                            val expense = navBackStackEntry.arguments?.getString("expenseId")
                            ExpenseDetailScreen(expenseId = expense, viewModel = viewModel, navController = navController)
                        }
                        composable("${Routes.USER_DETAILS}/{userId}") { navBackStackEntry ->
                            viewModel.refreshGraphQlError()
                            val userId =
                                navBackStackEntry.arguments?.getString("userId")
                            UserDetailsScreen(
                                userId = userId,
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                        composable(Routes.USER_STATS){
                            viewModel.refreshGraphQlError()
                            StatsScreen(viewModel = viewModel, navController = navController)
                        }
                        composable("${Routes.GROUP_STATS}/{groupId}") {navBackStackEntry ->
                            viewModel.refreshGraphQlError()
                            val groupId = navBackStackEntry.arguments?.getString("groupId")
                            GroupStatsDetailScreen(viewModel = viewModel, navController = navController, groupId = groupId)
                        }
                        composable(Routes.USER_PAYMENT) {
                            viewModel.refreshGraphQlError()
                            UserPaymentScreen(viewModel = viewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
