package com.sentinel.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.R
import com.sentinel.domain.model.EventSeverity
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.components.StatusBadge
import com.sentinel.ui.theme.SentinelColors
import com.sentinel.util.toFormattedTimestamp

@Composable
fun EventLogScreen(
    onNavigateBack: () -> Unit,
    viewModel: EventLogViewModel = hiltViewModel()
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.event_log), onNavigateBack) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.updateSearch(it)
                },
                label = { Text(stringResource(R.string.search_events)) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    SentinelCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.eventType.name, style = MaterialTheme.typography.titleSmall)
                                if (event.ruleName != null) {
                                    Text(
                                        stringResource(R.string.rule_label, event.ruleName),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    event.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    stringResource(R.string.source_label, event.triggerSource.name),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                Text(
                                    event.timestamp.toFormattedTimestamp(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Spacer(Modifier.height(4.dp))
                                StatusBadge(
                                    event.severity.name,
                                    event.severity != EventSeverity.CRITICAL
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
