package com.sentinel.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sentinel.R
import com.sentinel.ui.components.SentinelCard
import com.sentinel.ui.components.SentinelTopBar
import com.sentinel.ui.components.SectionHeader

@Composable
fun UserGuideScreen(onNavigateBack: () -> Unit) {
    Scaffold(topBar = { SentinelTopBar(stringResource(R.string.user_guide), onNavigateBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GuideSection(R.string.guide_unlock_title, R.string.guide_unlock_body)
            GuideSection(R.string.guide_duress_title, R.string.guide_duress_body)
            GuideSection(R.string.guide_files_title, R.string.guide_files_body)
            GuideSection(R.string.guide_apps_title, R.string.guide_apps_body)
            GuideSection(R.string.guide_profiles_title, R.string.guide_profiles_body)
            GuideSection(R.string.guide_decoy_title, R.string.guide_decoy_body)
            GuideSection(R.string.guide_rules_title, R.string.guide_rules_body)
            GuideSection(R.string.guide_panic_title, R.string.guide_panic_body)
            GuideSection(R.string.guide_navigation_title, R.string.guide_navigation_body)
        }
    }
}

@Composable
private fun GuideSection(titleRes: Int, bodyRes: Int) {
    SectionHeader(stringResource(titleRes))
    SentinelCard {
        Text(
            stringResource(bodyRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
