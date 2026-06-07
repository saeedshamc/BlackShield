package com.sentinel.ui.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.R
import com.sentinel.domain.model.AppLanguage
import com.sentinel.navigation.SentinelRoutes
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.components.SectionHeader

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.settings), onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(stringResource(R.string.section_language))
            SentinelCard {
                Text(
                    stringResource(R.string.language_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                LanguageSelector(
                    selected = language,
                    onSelect = { selected ->
                        if (selected != language) {
                            viewModel.setLanguage(selected) { activity?.recreate() }
                        }
                    }
                )
            }

            SectionHeader(stringResource(R.string.section_security))
            SettingsItem(stringResource(R.string.duress_passwords), Icons.Default.Password, SentinelRoutes.DURESS_PASSWORD, onNavigate)
            SettingsItem(stringResource(R.string.backup_restore), Icons.Default.Backup, SentinelRoutes.BACKUP, onNavigate)

            SectionHeader(stringResource(R.string.section_permissions))
            SentinelCard {
                Text(stringResource(R.string.perm_accessibility), style = MaterialTheme.typography.bodySmall)
                Text(stringResource(R.string.perm_device_admin), style = MaterialTheme.typography.bodySmall)
            }

            SectionHeader(stringResource(R.string.section_about))
            SentinelCard {
                Text(stringResource(R.string.about_version), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.about_desc), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit
) {
    Column(Modifier.selectableGroup()) {
        LanguageOption(
            label = stringResource(R.string.language_english),
            selected = selected == AppLanguage.ENGLISH,
            onClick = { onSelect(AppLanguage.ENGLISH) }
        )
        LanguageOption(
            label = stringResource(R.string.language_persian),
            selected = selected == AppLanguage.PERSIAN,
            onClick = { onSelect(AppLanguage.PERSIAN) }
        )
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
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
        Row(verticalAlignment = Alignment.CenterVertically) {
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
    val messageKey by viewModel.messageKey.collectAsStateWithLifecycle()
    val messageArg by viewModel.messageArg.collectAsStateWithLifecycle()
    var mainPassword by remember { mutableStateOf("") }
    var duress1 by remember { mutableStateOf("") }
    var duress2 by remember { mutableStateOf("") }
    var duress3 by remember { mutableStateOf("") }

    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.duress_passwords), onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.duress_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            PasswordField(stringResource(R.string.main_password), mainPassword) { mainPassword = it }
            Button(onClick = { viewModel.setMainPassword(mainPassword) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save_main_password))
            }

            HorizontalDivider()

            PasswordField(stringResource(R.string.duress_1), duress1) { duress1 = it }
            Button(onClick = { viewModel.setDuressPassword(1, duress1) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save_duress_password, 1))
            }

            PasswordField(stringResource(R.string.duress_2), duress2) { duress2 = it }
            Button(onClick = { viewModel.setDuressPassword(2, duress2) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save_duress_password, 2))
            }

            PasswordField(stringResource(R.string.duress_3), duress3) { duress3 = it }
            Button(onClick = { viewModel.setDuressPassword(3, duress3) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.save_duress_password, 3))
            }

            messageKey?.let { key ->
                val text = when (key) {
                    "password_too_short" -> stringResource(R.string.password_too_short)
                    "main_password_saved" -> stringResource(R.string.main_password_saved)
                    "duress_password_saved" -> stringResource(R.string.duress_password_saved, messageArg ?: 0)
                    else -> key
                }
                Text(text, color = MaterialTheme.colorScheme.primary)
            }
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
    val exportKey by viewModel.exportKey.collectAsStateWithLifecycle()
    val exportArg by viewModel.exportArg.collectAsStateWithLifecycle()
    val importKey by viewModel.importKey.collectAsStateWithLifecycle()
    val importArg1 by viewModel.importArg1.collectAsStateWithLifecycle()
    val importArg2 by viewModel.importArg2.collectAsStateWithLifecycle()
    val importError by viewModel.importError.collectAsStateWithLifecycle()
    var importPayload by remember { mutableStateOf("") }

    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.backup_restore), onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.backup_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = viewModel::export, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Upload, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.export_backup))
            }
            exportKey?.let { key ->
                val text = when (key) {
                    "export_success" -> stringResource(R.string.export_success, exportArg?.toIntOrNull() ?: 0)
                    "export_failed" -> stringResource(R.string.export_failed, exportArg.orEmpty())
                    else -> key
                }
                Text(text, style = MaterialTheme.typography.bodySmall)
            }

            HorizontalDivider()

            OutlinedTextField(
                value = importPayload,
                onValueChange = { importPayload = it },
                label = { Text(stringResource(R.string.paste_backup)) },
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
                Text(stringResource(R.string.import_backup))
            }
            importKey?.let { key ->
                val text = when (key) {
                    "import_success" -> stringResource(
                        R.string.import_success,
                        importArg1 ?: 0,
                        importArg2 ?: 0
                    )
                    "import_failed" -> stringResource(R.string.import_failed, importError.orEmpty())
                    else -> key
                }
                Text(text, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
