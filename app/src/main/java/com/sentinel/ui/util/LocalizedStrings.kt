package com.sentinel.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sentinel.R
import com.sentinel.domain.model.Profile
import com.sentinel.domain.model.ProfileType

@Composable
fun profileDisplayName(profile: Profile): String {
    if (!profile.isDefault) return profile.name
    return when (profile.type) {
        ProfileType.NORMAL -> stringResource(R.string.profile_normal)
        ProfileType.TRAVEL -> stringResource(R.string.profile_travel)
        ProfileType.BORDER_CROSSING -> stringResource(R.string.profile_border)
        ProfileType.EMERGENCY -> stringResource(R.string.profile_emergency)
        ProfileType.CUSTOM -> profile.name
    }
}
