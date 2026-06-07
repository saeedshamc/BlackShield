package com.sentinel.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.R
import com.sentinel.navigation.SentinelRoutes
import com.sentinel.ui.components.*
import com.sentinel.ui.theme.SentinelColors
import com.sentinel.ui.util.profileDisplayName
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
                        Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.labelLarge)
                        Text(stringResource(R.string.dashboard_subtitle), style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(SentinelRoutes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.cd_settings))
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
                Icon(Icons.Default.Emergency, contentDescription = stringResource(R.string.cd_panic), tint = MaterialTheme.colorScheme.onError)
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
                        title = stringResource(R.string.active_profile),
                        value = state.activeProfile?.let { profileDisplayName(it) }
                            ?: stringResource(R.string.none),
                        icon = Icons.Default.Shield,
                        accentColor = SentinelColors.CyberCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(SentinelRoutes.PROFILES) }
                    )
                    DashboardCard(
                        title = stringResource(R.string.security_score),
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
                        title = stringResource(R.string.active_rules),
                        value = state.activeRulesCount.toString(),
                        icon = Icons.Default.Rule,
                        accentColor = SentinelColors.CyberPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(SentinelRoutes.RULES) }
                    )
                    DashboardCard(
                        title = stringResource(R.string.locked_apps),
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
                            Text(stringResource(R.string.emergency_actions), style = MaterialTheme.typography.titleMedium)
                            Text(
                                stringResource(R.string.emergency_actions_desc),
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
                            StatusBadge(stringResource(R.string.decoy_mode_active), active = true)
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { onNavigate(SentinelRoutes.DECOY) }) {
                            Text(stringResource(R.string.manage_decoy))
                        }
                    }
                }
            }

            item { SectionHeader(stringResource(R.string.quick_access)) }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple(stringResource(R.string.nav_rules), Icons.Default.Rule, SentinelRoutes.RULES),
                        Triple(stringResource(R.string.nav_profiles), Icons.Default.Person, SentinelRoutes.PROFILES),
                        Triple(stringResource(R.string.nav_decoy), Icons.Default.VisibilityOff, SentinelRoutes.DECOY),
                        Triple(stringResource(R.string.nav_events), Icons.Default.History, SentinelRoutes.EVENTS)
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

            item { SectionHeader(stringResource(R.string.recent_events)) }

            if (state.recentEvents.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_events),
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
