package com.sentinel.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sentinel.ui.applocker.AppLockerScreen
import com.sentinel.ui.dashboard.DashboardScreen
import com.sentinel.ui.decoy.DecoyConfigScreen
import com.sentinel.ui.decoy.DecoyContactsScreen
import com.sentinel.ui.decoy.DecoyGalleryScreen
import com.sentinel.ui.decoy.DecoyNotesScreen
import com.sentinel.ui.events.EventLogScreen
import com.sentinel.ui.panic.PanicCenterScreen
import com.sentinel.ui.profiles.ProfileManagerScreen
import com.sentinel.ui.rules.RuleBuilderScreen
import com.sentinel.ui.rules.RuleListScreen
import com.sentinel.ui.settings.BackupScreen
import com.sentinel.ui.settings.DuressPasswordScreen
import com.sentinel.ui.settings.SettingsScreen

@Composable
fun SentinelNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SentinelRoutes.DASHBOARD,
        modifier = modifier
    ) {
        composable(SentinelRoutes.DASHBOARD) {
            DashboardScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(SentinelRoutes.RULES) {
            RuleListScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateRule = { navController.navigate(SentinelRoutes.RULE_BUILDER) },
                onEditRule = { id -> navController.navigate(SentinelRoutes.ruleBuilder(id)) }
            )
        }
        composable(SentinelRoutes.RULE_BUILDER) {
            RuleBuilderScreen(
                ruleId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SentinelRoutes.RULE_BUILDER_WITH_ID,
            arguments = listOf(navArgument("ruleId") { type = NavType.LongType })
        ) { entry ->
            RuleBuilderScreen(
                ruleId = entry.arguments?.getLong("ruleId"),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(SentinelRoutes.PROFILES) {
            ProfileManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(SentinelRoutes.DECOY) {
            DecoyConfigScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(SentinelRoutes.DECOY_GALLERY) {
            DecoyGalleryScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(SentinelRoutes.DECOY_CONTACTS) {
            DecoyContactsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(SentinelRoutes.DECOY_NOTES) {
            DecoyNotesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(SentinelRoutes.APP_LOCKER) {
            AppLockerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(SentinelRoutes.PANIC) {
            PanicCenterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(SentinelRoutes.EVENTS) {
            EventLogScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(SentinelRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(SentinelRoutes.DURESS_PASSWORD) {
            DuressPasswordScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(SentinelRoutes.BACKUP) {
            BackupScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
