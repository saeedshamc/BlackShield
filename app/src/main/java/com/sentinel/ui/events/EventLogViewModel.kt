package com.sentinel.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.domain.model.SecurityEvent
import com.sentinel.domain.usecase.security.ObserveSecurityEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventLogViewModel @Inject constructor(
    private val observeEvents: ObserveSecurityEventsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val events: StateFlow<List<SecurityEvent>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) observeEvents() else observeEvents.search(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }
}
