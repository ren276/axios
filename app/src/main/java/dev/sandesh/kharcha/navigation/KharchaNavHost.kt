package dev.sandesh.kharcha.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sandesh.kharcha.security.AppLockManager
import dev.sandesh.kharcha.feature.transactions.TransactionsRoute
import dev.sandesh.kharcha.feature.accounts.AccountsRoute
import dev.sandesh.kharcha.feature.budget.BudgetRoute
import dev.sandesh.kharcha.feature.reports.ReportsRoute
import dev.sandesh.kharcha.feature.settings.SettingsRoute
import dev.sandesh.kharcha.feature.onboarding.OnboardingRoute
import dev.sandesh.kharcha.home.HomeRoute

sealed class TopLevelRoute(val route: String, val label: String, val icon: ImageVector) {
    object Home : TopLevelRoute("home", "Home", Icons.Default.Home)
    object Transactions : TopLevelRoute("transactions", "Transactions", Icons.Default.List)
    object Budget : TopLevelRoute("budget", "Budget", Icons.Default.AccountBalance)
    object Reports : TopLevelRoute("reports", "Reports", Icons.Default.BarChart)
    object Settings : TopLevelRoute("settings", "Settings", Icons.Default.Person)
}

private val topLevelRoutes = listOf(
    TopLevelRoute.Home,
    TopLevelRoute.Transactions,
    TopLevelRoute.Budget,
    TopLevelRoute.Reports,
    TopLevelRoute.Settings,
)

@Composable
fun KharchaNavHost(
    appLockManager: AppLockManager,
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                topLevelRoutes.forEach { topLevelRoute ->
                    NavigationBarItem(
                        icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.label) },
                        label = { Text(topLevelRoute.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == topLevelRoute.route } == true,
                        onClick = {
                            navController.navigate(topLevelRoute.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "onboarding", // Default to onboarding; viewmodel logic inside onboarding routes if done
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("onboarding") {
                // In a real app, you would check if onboarding is complete before showing this, 
                // or have the Home/Splash screen decide where to route. For simplicity, 
                // the OnboardingRoute handles its own completion state to navigate away.
                OnboardingRoute(navController = navController)
            }
            composable(TopLevelRoute.Home.route) {
                HomeRoute(navController = navController)
            }
            composable(TopLevelRoute.Transactions.route) {
                TransactionsRoute(navController = navController)
            }
            composable(TopLevelRoute.Budget.route) {
                BudgetRoute(navController = navController)
            }
            composable(TopLevelRoute.Reports.route) {
                ReportsRoute(navController = navController)
            }
            composable(TopLevelRoute.Settings.route) {
                SettingsRoute(navController = navController)
            }
        }
    }
}
