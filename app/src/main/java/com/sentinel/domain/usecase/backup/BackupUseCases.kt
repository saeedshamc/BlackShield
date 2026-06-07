package com.sentinel.domain.usecase.backup

import com.sentinel.data.repository.BackupRepository
import com.sentinel.domain.model.BackupData
import javax.inject.Inject

class ExportBackupUseCase @Inject constructor(private val repository: BackupRepository) {
    suspend operator fun invoke(): String = repository.exportBackup()
}

class ImportBackupUseCase @Inject constructor(private val repository: BackupRepository) {
    suspend operator fun invoke(payload: String): Result<BackupData> = repository.importBackup(payload)
}
