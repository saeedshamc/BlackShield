package com.sentinel.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sentinel.R
import com.sentinel.domain.model.InstalledAppInfo

@Composable
fun AppPickerDialog(
    apps: List<InstalledAppInfo>,
    onDismiss: () -> Unit,
    onSelect: (InstalledAppInfo) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(apps, query) {
        if (query.isBlank()) apps
        else apps.filter {
            it.appName.contains(query, ignoreCase = true) ||
                it.packageName.contains(query, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_installed_app)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text(stringResource(R.string.search_apps)) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.no_apps_found),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(filtered, key = { it.packageName }) { app ->
                            ListItem(
                                modifier = Modifier.clickable { onSelect(app) },
                                headlineContent = { Text(app.appName) },
                                supportingContent = { Text(app.packageName, style = MaterialTheme.typography.bodySmall) },
                                leadingContent = {
                                    Icon(Icons.Default.Android, contentDescription = null)
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
