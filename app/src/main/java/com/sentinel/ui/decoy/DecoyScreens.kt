package com.sentinel.ui.decoy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sentinel.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinel.domain.model.DecoyCategory
import com.sentinel.domain.usecase.decoy.ObserveDecoyContentUseCase
import com.sentinel.navigation.SentinelRoutes
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.theme.SentinelTheme
import com.sentinel.util.JsonUtil
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class DecoyViewModel @Inject constructor(
    observeDecoyContent: ObserveDecoyContentUseCase
) : ViewModel() {
    val gallery = observeDecoyContent(DecoyCategory.GALLERY)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val contacts = observeDecoyContent(DecoyCategory.CONTACTS)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val notes = observeDecoyContent(DecoyCategory.NOTES)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@AndroidEntryPoint
class DecoyLauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SentinelTheme {
                DecoyGalleryScreen(onNavigateBack = { finish() })
            }
        }
    }
}

@Composable
fun DecoyConfigScreen(
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.decoy_mode), onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.decoy_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            listOf(
                Triple(stringResource(R.string.fake_gallery), Icons.Default.Photo, SentinelRoutes.DECOY_GALLERY),
                Triple(stringResource(R.string.fake_contacts), Icons.Default.Contacts, SentinelRoutes.DECOY_CONTACTS),
                Triple(stringResource(R.string.fake_notes), Icons.Default.Note, SentinelRoutes.DECOY_NOTES)
            ).forEach { (title, icon, route) ->
                SentinelCard(onClick = { onNavigate(route) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text(title, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun DecoyGalleryScreen(
    onNavigateBack: () -> Unit,
    viewModel: DecoyViewModel = hiltViewModel()
) {
    val items by viewModel.gallery.collectAsStateWithLifecycle()
    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.gallery), onNavigateBack) }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items) { item ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF37474F)),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        item.title,
                        modifier = Modifier.padding(4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DecoyContactsScreen(
    onNavigateBack: () -> Unit,
    viewModel: DecoyViewModel = hiltViewModel()
) {
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.contacts), onNavigateBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(contacts) { contact ->
                val data = JsonUtil.fromJsonOrNull<Map<String, String>>(contact.content)
                ListItem(
                    headlineContent = { Text(data?.get("name") ?: contact.title) },
                    supportingContent = { Text(data?.get("phone") ?: "") },
                    leadingContent = {
                        Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.padding(8.dp))
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun DecoyNotesScreen(
    onNavigateBack: () -> Unit,
    viewModel: DecoyViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.notes), onNavigateBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                val data = JsonUtil.fromJsonOrNull<Map<String, String>>(note.content)
                SentinelCard {
                    Text(data?.get("title") ?: note.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        data?.get("body") ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
