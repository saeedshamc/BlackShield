package com.sentinel.ui.rules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.R
import com.sentinel.domain.model.ActionType
import com.sentinel.domain.model.DuressActionCatalog
import com.sentinel.domain.model.ProfileType
import com.sentinel.domain.model.TriggerType
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.util.ActionLabels

@Composable
fun RuleListScreen(
    onNavigateBack: () -> Unit,
    onCreateRule: () -> Unit,
    onEditRule: (Long) -> Unit,
    viewModel: RuleListViewModel = hiltViewModel()
) {
    val rules by viewModel.rules.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { SentinelTopBar(stringResource(R.string.automation_rules), onNavigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRule) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { padding ->
        if (rules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_rules))
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
                                val triggerName = ActionLabels.triggerName(rule.trigger.type)
                                val actionNames = buildString {
                                    rule.actions.forEachIndexed { index, action ->
                                        if (index > 0) append(", ")
                                        append(stringResource(ActionLabels.actionLabel(action.type)))
                                    }
                                }
                                Text(
                                    stringResource(R.string.rule_format, triggerName, actionNames),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = rule.enabled,
                                onCheckedChange = { viewModel.toggleRule(rule.id, it) }
                            )
                            IconButton(onClick = { viewModel.deleteRule(rule.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.cancel))
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
    var addActionExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(ruleId) { viewModel.loadRule(ruleId) }

    val title = if (ruleId != null) stringResource(R.string.edit_rule) else stringResource(R.string.new_rule)

    Scaffold(
        topBar = { SentinelTopBar(title, onNavigateBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text(stringResource(R.string.rule_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::updateDescription,
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text(stringResource(R.string.trigger_if), style = MaterialTheme.typography.titleSmall)
            ExposedDropdownMenuBox(expanded = triggerExpanded, onExpandedChange = { triggerExpanded = it }) {
                OutlinedTextField(
                    value = ActionLabels.triggerName(state.triggerType),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(triggerExpanded) }
                )
                ExposedDropdownMenu(expanded = triggerExpanded, onDismissRequest = { triggerExpanded = false }) {
                    TriggerType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(ActionLabels.triggerName(type)) },
                            onClick = { viewModel.updateTriggerType(type); triggerExpanded = false }
                        )
                    }
                }
            }

            if (state.triggerType in RuleBuilderViewModel.thresholdTriggers) {
                OutlinedTextField(
                    value = state.triggerThreshold.toString(),
                    onValueChange = { viewModel.updateThreshold(it.toIntOrNull() ?: 5) },
                    label = { Text(stringResource(R.string.threshold_count)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(stringResource(R.string.actions_then), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(R.string.actions_then_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            state.actions.forEachIndexed { index, action ->
                SentinelCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            ActionLabels.actionName(action.type),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        if (state.actions.size > 1) {
                            IconButton(onClick = { viewModel.removeAction(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.cancel))
                            }
                        }
                    }
                    if (action.type == ActionType.ACTIVATE_PROFILE) {
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.select_profile), style = MaterialTheme.typography.labelMedium)
                        ProfileType.entries.filter { it != ProfileType.CUSTOM }.forEach { type ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = action.profileType == type.name,
                                    onClick = { viewModel.updateActionProfile(index, type.name) }
                                )
                                Text(type.name)
                            }
                        }
                    }
                }
            }

            ExposedDropdownMenuBox(expanded = addActionExpanded, onExpandedChange = { addActionExpanded = it }) {
                OutlinedButton(
                    onClick = { addActionExpanded = true },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add_action))
                }
                ExposedDropdownMenu(expanded = addActionExpanded, onDismissRequest = { addActionExpanded = false }) {
                    DuressActionCatalog.availableActions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(ActionLabels.actionName(item.type)) },
                            onClick = {
                                viewModel.addAction(item.type)
                                addActionExpanded = false
                            }
                        )
                    }
                    ActionType.LOG_EVENT.let { type ->
                        DropdownMenuItem(
                            text = { Text(ActionLabels.actionName(type)) },
                            onClick = {
                                viewModel.addAction(type)
                                addActionExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.save(onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.name.isNotBlank() && state.actions.isNotEmpty()
            ) {
                Text(stringResource(R.string.save_rule))
            }
        }
    }
}
