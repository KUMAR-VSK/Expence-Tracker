package com.example.expensetracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.presentation.navigation.Screen
import com.example.expensetracker.presentation.screens.*
import com.example.expensetracker.presentation.viewmodel.AnalyticsViewModel
import com.example.expensetracker.presentation.viewmodel.BudgetViewModel
import com.example.expensetracker.presentation.viewmodel.CategoryViewModel
import com.example.expensetracker.presentation.viewmodel.DashboardViewModel
import com.example.expensetracker.presentation.viewmodel.MainViewModel
import com.example.expensetracker.presentation.viewmodel.ProfileViewModel
import com.example.expensetracker.presentation.viewmodel.SettingsViewModel
import com.example.expensetracker.presentation.viewmodel.TransactionViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainTabs = listOf(
        Screen.Dashboard.route,
        Screen.History.route,
        Screen.Analytics.route,
        Screen.Budget.route,
        Screen.Settings.route
    )
    val showBottomBar = currentRoute in mainTabs

    val mainViewModel: MainViewModel = hiltViewModel()
    val settings by mainViewModel.settings.collectAsState()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Dashboard.route,
                        onClick = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.History.route,
                        onClick = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(Screen.Dashboard.route)
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = "Transactions") },
                        label = { Text("History") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Analytics.route,
                        onClick = {
                            navController.navigate(Screen.Analytics.route) {
                                popUpTo(Screen.Dashboard.route)
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Analytics") },
                        label = { Text("Analytics") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Budget.route,
                        onClick = {
                            navController.navigate(Screen.Budget.route) {
                                popUpTo(Screen.Dashboard.route)
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Outlined.TrendingUp, contentDescription = "Budget") },
                        label = { Text("Budget") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(Screen.Dashboard.route)
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    isPinLocked = settings.isPinLocked,
                    onNavigateNext = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.SecurityLock.route) {
                SecurityLockScreen(
                    viewModel = mainViewModel,
                    onPinCorrect = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.SecurityLock.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                val dbVM: DashboardViewModel = hiltViewModel()
                DashboardScreen(
                    viewModel = dbVM,
                    userName = settings.userName,
                    currencySymbol = settings.currencySymbol,
                    precision = settings.decimalPrecision,
                    dateFormat = settings.dateFormat,
                    onAddTransactionClick = {
                        navController.navigate(Screen.AddEditTransaction.createRoute())
                    },
                    onTransactionClick = { id ->
                        navController.navigate(Screen.TransactionDetails.createRoute(id))
                    },
                    onSeeAllClick = {
                        navController.navigate(Screen.History.route)
                    }
                )
            }

            composable(Screen.History.route) {
                val txVM: TransactionViewModel = hiltViewModel()
                HistoryScreen(
                    viewModel = txVM,
                    currencySymbol = settings.currencySymbol,
                    precision = settings.decimalPrecision,
                    dateFormat = settings.dateFormat,
                    onTransactionClick = { id ->
                        navController.navigate(Screen.TransactionDetails.createRoute(id))
                    }
                )
            }

            composable(Screen.Analytics.route) {
                val anVM: AnalyticsViewModel = hiltViewModel()
                AnalyticsScreen(
                    viewModel = anVM,
                    currencySymbol = settings.currencySymbol,
                    precision = settings.decimalPrecision,
                    dateFormat = settings.dateFormat,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Budget.route) {
                val bdVM: BudgetViewModel = hiltViewModel()
                BudgetScreen(
                    viewModel = bdVM,
                    currencySymbol = settings.currencySymbol,
                    precision = settings.decimalPrecision,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToCategories = {
                        navController.navigate(Screen.Categories.route)
                    }
                )
            }

            composable(Screen.Settings.route) {
                val setVM: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = setVM,
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToCategories = { navController.navigate(Screen.Categories.route) },
                    onNavigateToPayments = { navController.navigate(Screen.PaymentMethods.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) }
                )
            }

            composable(
                route = Screen.AddEditTransaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                val txVM: TransactionViewModel = hiltViewModel()
                val idStr = backStackEntry.arguments?.getString("transactionId")
                val id = idStr?.toLongOrNull()
                AddEditTransactionScreen(
                    viewModel = txVM,
                    transactionId = id,
                    currencySymbol = settings.currencySymbol,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToCategories = { navController.navigate(Screen.Categories.route) },
                    onNavigateToPayments = { navController.navigate(Screen.PaymentMethods.route) }
                )
            }

            composable(
                route = Screen.TransactionDetails.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val txVM: TransactionViewModel = hiltViewModel()
                val id = backStackEntry.arguments?.getLong("transactionId") ?: 0L
                TransactionDetailsScreen(
                    viewModel = txVM,
                    transactionId = id,
                    currencySymbol = settings.currencySymbol,
                    precision = settings.decimalPrecision,
                    dateFormat = settings.dateFormat,
                    onEditClick = { txId ->
                        navController.navigate(Screen.AddEditTransaction.createRoute(txId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Categories.route) {
                val catVM: CategoryViewModel = hiltViewModel()
                CategoriesScreen(
                    viewModel = catVM,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.PaymentMethods.route) {
                val txVM: TransactionViewModel = hiltViewModel()
                PaymentMethodsScreen(
                    viewModel = txVM,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                val prVM: ProfileViewModel = hiltViewModel()
                ProfileScreen(
                    viewModel = prVM,
                    currencySymbol = settings.currencySymbol,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.About.route) {
                AboutScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
