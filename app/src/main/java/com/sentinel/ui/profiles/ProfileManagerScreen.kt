package com.sentinel.ui.profiles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.components.StatusBadge

@Composable
fun ProfileManagerScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileManagerViewModel = hiltViewModel()
) {
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()

    Scaffold(topBar = { SentinelTopBar("Emergency Profiles", onNavigateBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Switch profiles instantly to change your security posture.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(profiles, key = { it.id }) { profile ->
                SentinelCard(onClick = { viewModel.activate(profile.id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    profile.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = runCatching {
                                        Color(android.graphics.Color.parseColor(profile.color))
                                    }.getOrDefault(MaterialTheme.colorScheme.primary)
                                )
                                if (profile.isActive) {
                                    Spacer(Modifier.width(8.dp))
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                profile.type.name.replace('_', ' '),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                StatusBadge("${profile.lockedApps.size} locked", profile.lockedApps.isNotEmpty())
                                StatusBadge("${profile.hiddenApps.size} hidden", profile.hiddenApps.isNotEmpty())
                                if (profile.decoySettings.enabled) {
                                    StatusBadge("DECOY", true)
                                }
                            }
                        }
                        if (!profile.isActive) {
                            OutlinedButton(onClick = { viewModel.activate(profile.id) }) {
                                Text("Activate")
                            }
                        }
                    }
                }
            }
        }
    }
}
