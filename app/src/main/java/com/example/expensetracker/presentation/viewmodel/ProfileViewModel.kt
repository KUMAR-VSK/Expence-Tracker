package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.AppSettings
import com.example.expensetracker.domain.usecase.GetSettingsUseCase
import com.example.expensetracker.domain.usecase.SaveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun updateProfile(userName: String, savingGoal: Double) {
        viewModelScope.launch {
            val current = getSettingsUseCase.getDirect()
            saveSettingsUseCase(
                current.copy(
                    userName = userName,
                    savingGoal = savingGoal
                )
            )
        }
    }
}
