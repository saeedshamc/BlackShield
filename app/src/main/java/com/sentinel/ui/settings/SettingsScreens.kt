package com.sentinel.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.navigation.SentinelRoutes
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.components.SectionHeader

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(topBar = { SentinelTopBar("Settings", onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader("Security")
            SettingsItem("Duress Passwords", Icons.Default.Password, SentinelRoutes.DURESS_PASSWORD, onNavigate)
            SettingsItem("Backup & Restore", Icons.Default.Backup, SentinelRoutes.BACKUP, onNavigate)

            SectionHeader("Permissions")
            SentinelCard {
                Text("Enable Accessibility Service for app monitoring and automation.", style = MaterialTheme.typography.bodySmall)
                Text("Enable Device Admin for intrusion detection.", style = MaterialTheme.typography.bodySmall)
            }

            SectionHeader("About")
            SentinelCard {
                Text("Sentinel v1.0.0", style = MaterialTheme.typography.titleMedium)
                Text("Offline personal security automation platform.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    route: String,
    onNavigate: (String) -> Unit
) {
    SentinelCard(onClick = { onNavigate(route) }) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun DuressPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: DuressPasswordViewModel = hiltViewModel()
) {
    val message by viewModel.message.collectAsStateWithLifecycle()
    var mainPassword by remember { mutableStateOf("") }
    var duress1 by remember { mutableStateOf("") }
    var duress2 by remember { mutableStateOf("") }
    var duress3 by remember { mutableStateOf("") }

    Scaffold(topBar = { SentinelTopBar("Duress Passwords", onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Configure multiple passwords. Duress passwords trigger defensive actions silently.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            PasswordField("Main Password (normal unlock)", mainPassword) { mainPassword = it }
            Button(onClick = { viewModel.setMainPassword(mainPassword) }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Main Password")
            }

            HorizontalDivider()

            PasswordField("Duress 1 → Decoy Mode", duress1) { duress1 = it }
            Button(onClick = { viewModel.setDuressPassword(1, duress1) }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Duress Password 1")
            }

            PasswordField("Duress 2 → Lock Apps", duress2) { duress2 = it }
            Button(onClick = { viewModel.setDuressPassword(2, duress2) }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Duress Password 2")
            }

            PasswordField("Duress 3 → Emergency Actions", duress3) { duress3 = it }
            Button(onClick = { viewModel.setDuressPassword(3, duress3) }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Duress Password 3")
            }

            message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
private fun PasswordField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun BackupScreen(
    onNavigateBack: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val exportResult by viewModel.exportResult.collectAsStateWithLifecycle()
    val importResult by viewModel.importResult.collectAsStateWithLifecycle()
    var importPayload by remember { mutableStateOf("") }

    Scaffold(topBar = { SentinelTopBar("Backup & Restore", onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Export and import encrypted settings. All data stays on-device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = viewModel::export, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Upload, null)
                Spacer(Modifier.width(8.dp))
                Text("Export Encrypted Backup")
            }
            exportResult?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

            HorizontalDivider()

            OutlinedTextField(
                value = importPayload,
                onValueChange = { importPayload = it },
                label = { Text("Paste backup payload") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
            Button(
                onClick = { viewModel.import(importPayload) },
                modifier = Modifier.fillMaxWidth(),
                enabled = importPayload.isNotBlank()
            ) {
                Icon(Icons.Default.Download, null)
                Spacer(Modifier.width(8.dp))
                Text("Import Backup")
            }
            importResult?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
