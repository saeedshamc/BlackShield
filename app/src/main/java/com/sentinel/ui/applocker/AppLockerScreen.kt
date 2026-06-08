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
import com.sentinel.data.repository.InstalledAppsRepository
import com.sentinel.domain.model.InstalledAppInfo
import com.sentinel.domain.model.LockMethod
import com.sentinel.domain.model.LockedApp
import com.sentinel.domain.usecase.applocker.*
import com.sentinel.ui.components.AppPickerDialog
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
    private val lockAllApps: LockAllAppsUseCase,
    private val installedAppsRepository: InstalledAppsRepository
) : ViewModel() {

    val lockedApps: StateFlow<List<LockedApp>> = observeLockedApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    var showAppPicker by mutableStateOf(false)
        private set

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _installedApps.value = installedAppsRepository.getLaunchableApps()
        }
    }

    fun showPicker() {
        loadInstalledApps()
        showAppPicker = true
    }

    fun hidePicker() { showAppPicker = false }

    fun addApp(app: InstalledAppInfo, method: LockMethod = LockMethod.PIN) = viewModelScope.launch {
        saveLockedApp(
            LockedApp(
                packageName = app.packageName,
                appName = app.appName,
                lockMethod = method,
                lockImmediately = true,
                lockOnScreenOff = true,
                isLocked = true
            )
        )
        hidePicker()
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
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()

    if (viewModel.showAppPicker) {
        AppPickerDialog(
            apps = installedApps,
            onDismiss = viewModel::hidePicker,
            onSelect = { viewModel.addApp(it) }
        )
    }

    Scaffold(
        topBar = { SentinelTopBar(stringResource(R.string.app_locker), onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showPicker) {
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
            Text(
                stringResource(R.string.app_locker_hint),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
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
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.cancel))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
