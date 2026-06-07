package com.sentinel.ui.panic

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.ui.components.SectionHeader
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.theme.SentinelColors

@Composable
fun PanicCenterScreen(
    onNavigateBack: () -> Unit,
    viewModel: PanicCenterViewModel = hiltViewModel()
) {
    val lastAction by viewModel.lastAction.collectAsStateWithLifecycle()

    Scaffold(topBar = { SentinelTopBar("Panic Control Center", onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Instant defensive actions. Also accessible via Quick Settings tile and hardware triggers.",
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
                Text("FULL PANIC SEQUENCE")
            }

            SectionHeader("Individual Actions")

            PanicActionButton("Activate Emergency Profile", Icons.Default.Shield, viewModel::executeEmergencyProfile)
            PanicActionButton("Lock All Apps", Icons.Default.Lock, viewModel::lockAllApps)
            PanicActionButton("Enable Decoy Mode", Icons.Default.VisibilityOff, viewModel::activateDecoy)
            PanicActionButton("Suppress Notifications", Icons.Default.NotificationsOff, viewModel::suppressNotifications)

            SectionHeader("Activation Methods")
            SentinelCard {
                listOf(
                    "Quick Settings Tile",
                    "Floating Panic Button",
                    "Power Button ×5",
                    "Power + Volume Up",
                    "Secret Gesture"
                ).forEach { method ->
                    Text("• $method", style = MaterialTheme.typography.bodySmall)
                }
            }

            lastAction?.let { action ->
                SentinelCard {
                    Text("Last action: $action", color = SentinelColors.CyberGreen)
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
