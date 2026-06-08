package com.sentinel.ui.panic

import androidx.compose.foundation.layout.*
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
import com.sentinel.ui.components.SectionHeader
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.theme.SentinelColors

@Composable
fun PanicCenterScreen(
    onNavigateBack: () -> Unit,
    viewModel: PanicCenterViewModel = hiltViewModel()
) {
    val lastActionKey by viewModel.lastActionKey.collectAsStateWithLifecycle()

    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.panic_center), onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.panic_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = viewModel::fullPanic,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SentinelColors.CyberRed)
            ) {
                Icon(Icons.Default.Emergency, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.full_panic))
            }

            SectionHeader(stringResource(R.string.individual_actions))

            PanicActionButton(stringResource(R.string.panic_emergency_profile), Icons.Default.Shield, viewModel::executeEmergencyProfile)
            PanicActionButton(stringResource(R.string.panic_lock_apps), Icons.Default.Lock, viewModel::lockAllApps)
            PanicActionButton(stringResource(R.string.panic_decoy), Icons.Default.VisibilityOff, viewModel::activateDecoy)
            PanicActionButton(stringResource(R.string.panic_notifications), Icons.Default.NotificationsOff, viewModel::suppressNotifications)

            SectionHeader(stringResource(R.string.activation_methods))
            SentinelCard {
                listOf(
                    stringResource(R.string.method_qs_tile),
                    stringResource(R.string.method_floating),
                    stringResource(R.string.method_power_x5),
                    stringResource(R.string.method_power_volume),
                    stringResource(R.string.method_gesture)
                ).forEach { method ->
                    Text("• $method", style = MaterialTheme.typography.bodySmall)
                }
            }

            lastActionKey?.let { key ->
                val message = when (key) {
                    "action_emergency_profile" -> stringResource(R.string.panic_result_emergency_profile)
                    "action_lock_apps" -> stringResource(R.string.panic_result_lock_apps)
                    "action_decoy" -> stringResource(R.string.panic_result_decoy)
                    "action_notifications" -> stringResource(R.string.action_notifications)
                    "action_full_panic" -> stringResource(R.string.action_full_panic)
                    else -> key
                }
                SentinelCard {
                    Text(stringResource(R.string.last_action, message), color = SentinelColors.CyberGreen)
                }
            }
        }
    }
}

@Composable
private fun PanicActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}
