package com.sentinel.data.repository

import com.sentinel.data.local.dao.RuleDao
import com.sentinel.data.mapper.EntityMappers.toDomain
import com.sentinel.data.mapper.EntityMappers.toEntity
import com.sentinel.domain.model.Rule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleRepository @Inject constructor(
    private val ruleDao: RuleDao
) {
    fun observeAllRules(): Flow<List<Rule>> =
        ruleDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeEnabledRules(): Flow<List<Rule>> =
        ruleDao.observeEnabled().map { list -> list.map { it.toDomain() } }

    fun observeEnabledCount(): Flow<Int> = ruleDao.observeEnabledCount()

    suspend fun getRuleById(id: Long): Rule? = ruleDao.getById(id)?.toDomain()

    suspend fun saveRule(rule: Rule): Long {
        val entity = rule.copy(updatedAt = System.currentTimeMillis()).toEntity()
        return if (entity.id == 0L) ruleDao.insert(entity) else {
            ruleDao.update(entity)
            entity.id
        }
    }

    suspend fun deleteRule(id: Long) = ruleDao.deleteById(id)

    suspend fun toggleRule(id: Long, enabled: Boolean) {
        ruleDao.getById(id)?.let { ruleDao.update(it.copy(enabled = enabled)) }
    }
}
