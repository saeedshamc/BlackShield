package com.sentinel.ui.unlock

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.R

@Composable
fun UnlockScreen(
    onUnlocked: () -> Unit,
    viewModel: UnlockViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Shield, null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.unlock_title), style = MaterialTheme.typography.headlineMedium)
            Text(
                stringResource(R.string.unlock_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::updatePassword,
                label = { Text(stringResource(R.string.unlock_password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.error != null
            )

            state.error?.let { error ->
                val message = when (error) {
                    UnlockError.EMPTY -> stringResource(R.string.unlock_error_empty)
                    UnlockError.INVALID -> stringResource(R.string.unlock_error_invalid)
                }
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.submit(onUnlocked) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Lock, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.unlock_button))
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.unlock_duress_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
