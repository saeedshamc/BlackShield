package com.sentinel.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.R
import com.sentinel.di.AppLockEntryPoint
import com.sentinel.domain.model.ActionType
import com.sentinel.domain.model.DuressActionCatalog
import com.sentinel.domain.model.ProfileType
import com.sentinel.domain.model.SensitiveFileTarget
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.components.SectionHeader
import com.sentinel.ui.util.ActionLabels
import com.sentinel.util.UriFileHelper
import dagger.hilt.android.EntryPointAccessors

@Composable
fun DuressConfigScreen(
    onNavigateBack: () -> Unit,
    viewModel: DuressConfigViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appLockCoordinator = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AppLockEntryPoint::class.java
        ).appLockCoordinator()
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val messageKey by viewModel.messageKey.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(messageKey) {
        messageKey?.let { key ->
            snackbarHostState.showSnackbar(messageText(context, key))
            viewModel.clearMessage()
        }
    }

    var pendingDuressForFiles by remember { mutableStateOf<Int?>(null) }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            UriFileHelper.takePersistableAccess(context, uri)
            val name = UriFileHelper.queryDisplayName(context, uri)
            viewModel.addSensitiveFileFromUri(name, uri.toString(), isFolder = false, assignToDuress = pendingDuressForFiles)
        }
        pendingDuressForFiles = null
    }

    val folderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            UriFileHelper.takePersistableAccess(context, it)
            val name = UriFileHelper.queryDisplayName(context, it)
            viewModel.addSensitiveFileFromUri(name, it.toString(), isFolder = true, assignToDuress = pendingDuressForFiles)
        }
        pendingDuressForFiles = null
    }

    fun launchFilePicker(duressIndex: Int? = null) {
        appLockCoordinator.suppressLockOnNextStop()
        pendingDuressForFiles = duressIndex
        filePicker.launch(arrayOf("*/*"))
    }

    fun launchFolderPicker(duressIndex: Int? = null) {
        appLockCoordinator.suppressLockOnNextStop()
        pendingDuressForFiles = duressIndex
        folderPicker.launch(null)
    }

    Scaffold(
        topBar = { SentinelTopBar(stringResource(R.string.duress_passwords), onNavigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    stringResource(R.string.duress_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!state.mainPasswordSet) {
                item {
                    SentinelCard {
                        Text(
                            stringResource(R.string.main_password_required_first),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                SectionHeader(stringResource(R.string.main_password))
                SentinelCard {
                    if (state.mainPasswordSet) {
                        Text(
                            stringResource(R.string.main_password_set_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = state.mainPassword,
                        onValueChange = viewModel::updateMainPassword,
                        label = { Text(stringResource(R.string.enter_new_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = viewModel::saveMainPassword, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.save_main_password))
                    }
                }
            }

            item {
                DuressLevelSection(
                    index = 1,
                    title = stringResource(R.string.duress_level, 1),
                    passwordSet = state.duress1Set,
                    password = state.duress1Password,
                    onPasswordChange = { viewModel.updateDuressPassword(1, it) },
                    selectedActions = state.duress1Actions,
                    profile = state.duress1Profile,
                    assignedFiles = state.sensitiveFiles.filter { it.deleteOnDuress1 },
                    onToggleAction = { viewModel.toggleAction(1, it) },
                    onProfileChange = { viewModel.updateProfile(1, it) },
                    onSave = { viewModel.saveDuress(1) },
                    onPickFiles = { launchFilePicker(1) },
                    onPickFolder = { launchFolderPicker(1) }
                )
            }
            item {
                DuressLevelSection(
                    index = 2,
                    title = stringResource(R.string.duress_level, 2),
                    passwordSet = state.duress2Set,
                    password = state.duress2Password,
                    onPasswordChange = { viewModel.updateDuressPassword(2, it) },
                    selectedActions = state.duress2Actions,
                    profile = state.duress2Profile,
                    assignedFiles = state.sensitiveFiles.filter { it.deleteOnDuress2 },
                    onToggleAction = { viewModel.toggleAction(2, it) },
                    onProfileChange = { viewModel.updateProfile(2, it) },
                    onSave = { viewModel.saveDuress(2) },
                    onPickFiles = { launchFilePicker(2) },
                    onPickFolder = { launchFolderPicker(2) }
                )
            }
            item {
                DuressLevelSection(
                    index = 3,
                    title = stringResource(R.string.duress_level, 3),
                    passwordSet = state.duress3Set,
                    password = state.duress3Password,
                    onPasswordChange = { viewModel.updateDuressPassword(3, it) },
                    selectedActions = state.duress3Actions,
                    profile = state.duress3Profile,
                    assignedFiles = state.sensitiveFiles.filter { it.deleteOnDuress3 },
                    onToggleAction = { viewModel.toggleAction(3, it) },
                    onProfileChange = { viewModel.updateProfile(3, it) },
                    onSave = { viewModel.saveDuress(3) },
                    onPickFiles = { launchFilePicker(3) },
                    onPickFolder = { launchFolderPicker(3) }
                )
            }

            item {
                SectionHeader(stringResource(R.string.sensitive_files_title))
                SentinelCard {
                    Text(
                        stringResource(R.string.sensitive_files_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { launchFilePicker(null) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.InsertDriveFile, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.pick_files))
                        }
                        OutlinedButton(
                            onClick = { launchFolderPicker(null) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FolderOpen, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.pick_folder))
                        }
                    }
                }
            }

            if (state.sensitiveFiles.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_sensitive_files),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.sensitiveFiles, key = { it.id }) { file ->
                    SensitiveFileCard(
                        file = file,
                        onToggleDuress1 = { viewModel.toggleFileDuress(file.id, 1) },
                        onToggleDuress2 = { viewModel.toggleFileDuress(file.id, 2) },
                        onToggleDuress3 = { viewModel.toggleFileDuress(file.id, 3) },
                        onRemove = { viewModel.removeSensitiveFile(file.id) }
                    )
                }
            }
        }
    }
}

private fun messageText(context: android.content.Context, key: String): String = when (key) {
    "password_too_short", "too_short" -> context.getString(R.string.password_too_short)
    "same_as_main" -> context.getString(R.string.password_same_as_main)
    "same_as_other_duress" -> context.getString(R.string.password_same_as_other_duress)
    "required" -> context.getString(R.string.duress_password_required)
    "no_actions" -> context.getString(R.string.duress_actions_required)
    "main_password_required_first" -> context.getString(R.string.main_password_required_first)
    "main_password_saved" -> context.getString(R.string.main_password_saved)
    "duress_config_saved" -> context.getString(R.string.duress_config_saved)
    "file_added" -> context.getString(R.string.file_added_success)
    else -> key
}

@Composable
private fun DuressLevelSection(
    index: Int,
    title: String,
    passwordSet: Boolean,
    password: String,
    onPasswordChange: (String) -> Unit,
    selectedActions: Set<ActionType>,
    profile: String,
    assignedFiles: List<SensitiveFileTarget>,
    onToggleAction: (ActionType) -> Unit,
    onProfileChange: (String) -> Unit,
    onSave: () -> Unit,
    onPickFiles: () -> Unit,
    onPickFolder: () -> Unit
) {
    SectionHeader(title)
    SentinelCard {
        if (passwordSet) {
            Text(
                stringResource(R.string.duress_password_set_hint, index),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                stringResource(R.string.duress_keep_password_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = {
                Text(
                    if (passwordSet) stringResource(R.string.duress_change_password)
                    else stringResource(R.string.enter_new_password)
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.duress_actions_label), style = MaterialTheme.typography.titleSmall)
        Text(
            stringResource(R.string.duress_unlock_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        DuressActionCatalog.availableActions.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedActions.contains(item.type),
                    onCheckedChange = { onToggleAction(item.type) }
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(ActionLabels.actionName(item.type))
                    if (item.isDestructive) {
                        Text(
                            stringResource(R.string.destructive_action),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                if (item.isDestructive) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (selectedActions.contains(ActionType.DELETE_SELECTED_FILES)) {
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.files_for_this_duress), style = MaterialTheme.typography.labelLarge)
            if (assignedFiles.isEmpty()) {
                Text(
                    stringResource(R.string.no_files_for_duress),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                assignedFiles.forEach { file ->
                    Text("• ${file.displayName}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onPickFiles, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.InsertDriveFile, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.pick_files))
                }
                OutlinedButton(onClick = onPickFolder, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.FolderOpen, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.pick_folder))
                }
            }
        }

        if (selectedActions.contains(ActionType.ACTIVATE_PROFILE)) {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.select_profile), style = MaterialTheme.typography.labelMedium)
            ProfileType.entries.filter { it != ProfileType.CUSTOM }.forEach { type ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = profile == type.name,
                        onClick = { onProfileChange(type.name) }
                    )
                    Text(type.name)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.save_duress_config, index))
        }
    }
}

@Composable
private fun SensitiveFileCard(
    file: SensitiveFileTarget,
    onToggleDuress1: () -> Unit,
    onToggleDuress2: () -> Unit,
    onToggleDuress3: () -> Unit,
    onRemove: () -> Unit
) {
    SentinelCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (file.isFolder) Icons.Default.FolderOpen else Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(file.displayName, style = MaterialTheme.typography.titleSmall)
                    Text(
                        if (file.isFolder) stringResource(R.string.folder_label)
                        else stringResource(R.string.file_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.cancel))
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = file.deleteOnDuress1,
                onClick = onToggleDuress1,
                label = { Text(stringResource(R.string.duress_short, 1)) }
            )
            FilterChip(
                selected = file.deleteOnDuress2,
                onClick = onToggleDuress2,
                label = { Text(stringResource(R.string.duress_short, 2)) }
            )
            FilterChip(
                selected = file.deleteOnDuress3,
                onClick = onToggleDuress3,
                label = { Text(stringResource(R.string.duress_short, 3)) }
            )
        }
    }
}
