package com.sentinel.ui.rules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar

@Composable
fun RuleListScreen(
    onNavigateBack: () -> Unit,
    onCreateRule: () -> Unit,
    onEditRule: (Long) -> Unit,
    viewModel: RuleListViewModel = hiltViewModel()
) {
    val rules by viewModel.rules.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { SentinelTopBar("Automation Rules", onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRule) {
                Icon(Icons.Default.Add, contentDescription = "Create Rule")
            }
        }
    ) { padding ->
        if (rules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No rules configured. Tap + to create one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rules, key = { it.id }) { rule ->
                    SentinelCard(onClick = { onEditRule(rule.id) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rule.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "IF ${rule.trigger.type.name} → ${rule.actions.firstOrNull()?.type?.name ?: "NONE"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = rule.enabled,
                                onCheckedChange = { viewModel.toggleRule(rule.id, it) }
                            )
                            IconButton(onClick = { viewModel.deleteRule(rule.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleBuilderScreen(
    ruleId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: RuleBuilderViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var triggerExpanded by remember { mutableStateOf(false) }
    var actionExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(ruleId) { viewModel.loadRule(ruleId) }

    Scaffold(
        topBar = { SentinelTopBar(if (ruleId != null) "Edit Rule" else "New Rule", onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text("Rule Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text("Trigger (IF)", style = MaterialTheme.typography.titleSmall)
            ExposedDropdownMenuBox(expanded = triggerExpanded, onExpandedChange = { triggerExpanded = it }) {
                OutlinedTextField(
                    value = state.triggerType.name,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(triggerExpanded) }
                )
                ExposedDropdownMenu(expanded = triggerExpanded, onDismissRequest = { triggerExpanded = false }) {
                    com.sentinel.domain.model.TriggerType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = { viewModel.updateTriggerType(type); triggerExpanded = false }
                        )
                    }
                }
            }

            if (state.triggerType.name.contains("ATTEMPT") || state.triggerType.name.contains("PRESS")) {
                OutlinedTextField(
                    value = state.triggerThreshold.toString(),
                    onValueChange = { viewModel.updateThreshold(it.toIntOrNull() ?: 5) },
                    label = { Text("Threshold / Count") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text("Action (THEN)", style = MaterialTheme.typography.titleSmall)
            ExposedDropdownMenuBox(expanded = actionExpanded, onExpandedChange = { actionExpanded = it }) {
                OutlinedTextField(
                    value = state.actionType.name,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(actionExpanded) }
                )
                ExposedDropdownMenu(expanded = actionExpanded, onDismissRequest = { actionExpanded = false }) {
                    com.sentinel.domain.model.ActionType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = { viewModel.updateActionType(type); actionExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.save(onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.name.isNotBlank()
            ) {
                Text("Save Rule")
            }
        }
    }
}
