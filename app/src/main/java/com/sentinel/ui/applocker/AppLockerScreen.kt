package com.sentinel.ui.applocker

import android.os.Bundle
import androidx.activity.compose.setContent
import com.sentinel.ui.base.LocaleAwareComponentActivity
import com.sentinel.ui.theme.SentinelLocaleProvider
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sentinel.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.sentinel.domain.model.LockMethod
import com.sentinel.domain.model.LockedApp
import com.sentinel.domain.usecase.applocker.*
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.components.StatusBadge
import com.sentinel.ui.theme.SentinelTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLockerViewModel @Inject constructor(
    observeLockedApps: ObserveLockedAppsUseCase,
    private val saveLockedApp: SaveLockedAppUseCase,
    private val removeLockedApp: RemoveLockedAppUseCase,
    private val lockAllApps: LockAllAppsUseCase
) : ViewModel() {

    val lockedApps: StateFlow<List<LockedApp>> = observeLockedApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var showAddDialog by mutableStateOf(false)
        private set

    fun showAdd() { showAddDialog = true }
    fun hideAdd() { showAddDialog = false }

    fun addApp(packageName: String, appName: String, method: LockMethod) = viewModelScope.launch {
        saveLockedApp(
            LockedApp(
                packageName = packageName,
                appName = appName,
                lockMethod = method,
                lockImmediately = true,
                lockOnScreenOff = true,
                isLocked = true
            )
        )
        hideAdd()
    }

    fun removeApp(app: LockedApp) = viewModelScope.launch { removeLockedApp(app) }
    fun lockAll() = viewModelScope.launch { lockAllApps() }
}

@AndroidEntryPoint
class LockOverlayActivity : LocaleAwareComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra("app_name") ?: "Protected App"
        setContent {
            SentinelLocaleProvider {
                SentinelTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Lock, null, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(16.dp))
                            Text(appName, style = MaterialTheme.typography.headlineMedium)
                            Text(stringResource(R.string.app_protected), style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(32.dp))
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text(stringResource(R.string.enter_pin)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppLockerScreen(
    onNavigateBack: () -> Unit,
    viewModel: AppLockerViewModel = hiltViewModel()
) {
    val apps by viewModel.lockedApps.collectAsStateWithLifecycle()

    if (viewModel.showAddDialog) {
        AddAppDialog(
            onDismiss = viewModel::hideAdd,
            onAdd = { pkg, name, method -> viewModel.addApp(pkg, name, method) }
        )
    }

    Scaffold(
        topBar = { SentinelTopBar(stringResource(R.string.app_locker), onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showAdd) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = viewModel::lockAll, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.lock_all_now))
                }
            }
            if (apps.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_locked_apps))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(apps, key = { it.id }) { app ->
                        SentinelCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(app.appName, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        app.packageName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    StatusBadge(app.lockMethod.name, true)
                                }
                                IconButton(onClick = { viewModel.removeApp(app) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddAppDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, LockMethod) -> Unit
) {
    var packageName by remember { mutableStateOf("") }
    var appName by remember { mutableStateOf("") }
    var method by remember { mutableStateOf(LockMethod.PIN) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.protect_app)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text(stringResource(R.string.app_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text(stringResource(R.string.package_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.lock_method, method.name), style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(packageName, appName, method) },
                enabled = packageName.isNotBlank() && appName.isNotBlank()
            ) { Text(stringResource(R.string.add)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
