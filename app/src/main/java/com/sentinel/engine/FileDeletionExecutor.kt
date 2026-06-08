package com.sentinel.engine

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.sentinel.data.repository.SensitiveFilesRepository
import com.sentinel.domain.model.PasswordType
import com.sentinel.domain.model.SensitiveFileTarget
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Deletes only user-configured sensitive files. Never performs a full device wipe.
 */
@Singleton
class FileDeletionExecutor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sensitiveFilesRepository: SensitiveFilesRepository
) {
    suspend fun deleteForDuressLevel(duressType: PasswordType): DeletionResult {
        val targets = sensitiveFilesRepository.getAll().filter { target ->
            target.enabled && when (duressType) {
                PasswordType.DURESS_1 -> target.deleteOnDuress1
                PasswordType.DURESS_2 -> target.deleteOnDuress2
                PasswordType.DURESS_3 -> target.deleteOnDuress3
                else -> false
            }
        }
        return deleteTargets(targets)
    }

    suspend fun deleteAllConfigured(): DeletionResult {
        val targets = sensitiveFilesRepository.getAll().filter { it.enabled }
        return deleteTargets(targets)
    }

    private fun deleteTargets(targets: List<SensitiveFileTarget>): DeletionResult {
        var deleted = 0
        var failed = 0
        val details = mutableListOf<String>()

        targets.forEach { target ->
            val success = deleteSingle(target)
            if (success) {
                deleted++
                details += "Deleted: ${target.displayName}"
            } else {
                failed++
                details += "Failed: ${target.displayName}"
            }
        }

        return DeletionResult(
            deletedCount = deleted,
            failedCount = failed,
            details = details.joinToString("; ")
        )
    }

    private fun deleteSingle(target: SensitiveFileTarget): Boolean {
        return runCatching {
            val path = target.filePath.trim()
            when {
                path.startsWith("content://") -> deleteContentUri(Uri.parse(path), target.isFolder)
                else -> {
                    val file = File(path)
                    if (file.isDirectory) file.deleteRecursively() else file.delete()
                }
            }
        }.onFailure { Log.e(TAG, "Delete failed for ${target.filePath}", it) }
            .getOrDefault(false)
    }

    private fun deleteContentUri(uri: Uri, isFolder: Boolean): Boolean {
        return if (isFolder) {
            val tree = DocumentFile.fromTreeUri(context, uri) ?: return false
            deleteDocumentTree(tree)
        } else {
            val doc = DocumentFile.fromSingleUri(context, uri)
            when {
                doc != null -> doc.delete()
                else -> context.contentResolver.delete(uri, null, null) > 0
            }
        }
    }

    private fun deleteDocumentTree(folder: DocumentFile): Boolean {
        folder.listFiles().forEach { child ->
            if (child.isDirectory) deleteDocumentTree(child) else child.delete()
        }
        return folder.delete()
    }

    companion object {
        private const val TAG = "FileDeletionExecutor"
    }
}

data class DeletionResult(
    val deletedCount: Int,
    val failedCount: Int,
    val details: String
)
