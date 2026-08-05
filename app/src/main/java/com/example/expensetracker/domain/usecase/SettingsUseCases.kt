package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.AppSettings
import com.example.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        return repository.getSettings()
    }

    suspend fun getDirect(): AppSettings {
        return repository.getSettingsDirect()
    }
}

class SaveSettingsUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(settings: AppSettings) {
        repository.saveSettings(settings)
    }
}
