package com.sentinel.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.navigation.SentinelRoutes
import com.sentinel.ui.components.*
import com.sentinel.ui.theme.SentinelColors
import com.sentinel.util.toFormattedTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SENTINEL", style = MaterialTheme.typography.labelLarge)
                        Text("Security Dashboard", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(SentinelRoutes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(SentinelRoutes.PANIC) },
                containerColor = SentinelColors.CyberRed
            ) {
                Icon(Icons.Default.Emergency, contentDescription = "Panic", tint = MaterialTheme.colorScheme.onError)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard(
                        title = "Active Profile",
                        value = state.activeProfile?.name ?: "None",
                        icon = Icons.Default.Shield,
                        accentColor = SentinelColors.CyberCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(SentinelRoutes.PROFILES) }
                    )
                    DashboardCard(
                        title = "Security Score",
                        value = "${state.securityStatus.overallScore}%",
                        icon = Icons.Default.Security,
                        accentColor = SentinelColors.CyberGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardCard(
                        title = "Active Rules",
                        value = state.activeRulesCount.toString(),
                        icon = Icons.Default.Rule,
                        accentColor = SentinelColors.CyberPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(SentinelRoutes.RULES) }
                    )
                    DashboardCard(
                        title = "Locked Apps",
                        value = state.lockedAppsCount.toString(),
                        icon = Icons.Default.Lock,
                        accentColor = SentinelColors.CyberOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(SentinelRoutes.APP_LOCKER) }
                    )
                }
            }

            item {
                SentinelCard(onClick = { onNavigate(SentinelRoutes.PANIC) }) {
                    Row {
                        Icon(Icons.Default.Warning, null, tint = SentinelColors.CyberRed)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Emergency Actions", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Panic center — quick defensive actions",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (state.decoyModeActive) {
                item {
                    SentinelCard {
                        Row {
                            StatusBadge(label = "DECOY MODE ACTIVE", active = true)
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { onNavigate(SentinelRoutes.DECOY) }) {
                            Text("Manage Decoy Environment")
                        }
                    }
                }
            }

            item { SectionHeader("Quick Access") }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple("Rules", Icons.Default.Rule, SentinelRoutes.RULES),
                        Triple("Profiles", Icons.Default.Person, SentinelRoutes.PROFILES),
                        Triple("Decoy", Icons.Default.VisibilityOff, SentinelRoutes.DECOY),
                        Triple("Events", Icons.Default.History, SentinelRoutes.EVENTS)
                    ).forEach { (label, icon, route) ->
                        OutlinedButton(
                            onClick = { onNavigate(route) },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                                Icon(icon, null, modifier = Modifier.size(20.dp))
                                Text(label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            item { SectionHeader("Recent Events") }

            if (state.recentEvents.isEmpty()) {
                item {
                    Text(
                        "No security events recorded yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.recentEvents) { event ->
                    SentinelCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.eventType.name, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    event.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                event.timestamp.toFormattedTimestamp(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
