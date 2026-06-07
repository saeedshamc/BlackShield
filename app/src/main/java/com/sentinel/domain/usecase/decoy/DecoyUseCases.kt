package com.sentinel.domain.usecase.decoy

import com.sentinel.data.repository.DecoyRepository
import com.sentinel.domain.model.DecoyCategory
import com.sentinel.domain.model.DecoyContent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDecoyContentUseCase @Inject constructor(private val repository: DecoyRepository) {
    operator fun invoke(category: DecoyCategory): Flow<List<DecoyContent>> =
        repository.observeByCategory(category)
}

class SaveDecoyContentUseCase @Inject constructor(private val repository: DecoyRepository) {
    suspend operator fun invoke(content: DecoyContent): Long = repository.saveContent(content)
}

class EnsureDecoyDefaultsUseCase @Inject constructor(private val repository: DecoyRepository) {
    suspend operator fun invoke() = repository.ensureDefaultContent()
}

class WipeDecoyDataUseCase @Inject constructor(private val repository: DecoyRepository) {
    suspend operator fun invoke() = repository.wipeAll()
}
