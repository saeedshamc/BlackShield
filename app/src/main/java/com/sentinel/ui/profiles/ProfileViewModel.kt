package com.sentinel.ui.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.domain.model.Profile
import com.sentinel.domain.usecase.profile.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileManagerViewModel @Inject constructor(
    observeProfiles: ObserveProfilesUseCase,
    private val activateProfile: ActivateProfileUseCase
) : ViewModel() {

    val profiles: StateFlow<List<Profile>> = observeProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun activate(profileId: Long) = viewModelScope.launch {
        activateProfile(profileId)
    }
}
