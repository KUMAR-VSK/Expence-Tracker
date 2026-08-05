package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.AppSettings
import com.example.expensetracker.domain.usecase.GetSettingsUseCase
import com.example.expensetracker.domain.usecase.SaveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    private val _isAppUnlocked = MutableStateFlow(false)
    val isAppUnlocked: StateFlow<Boolean> = _isAppUnlocked

    fun setAppUnlocked(unlocked: Boolean) {
        _isAppUnlocked.value = unlocked
    }

    suspend fun verifyPin(pin: String): Boolean {
        val currentSettings = getSettingsUseCase.getDirect()
        return if (currentSettings.isPinLocked && currentSettings.pinHash != null) {
            // Basic hash comparison (e.g. hashCode or plain check. For a local offline app, hashCode or basic hash is fine)
            currentSettings.pinHash == pin.hashCode().toString()
        } else {
            true
        }
    }

    fun setupPinLock(pin: String?, isEnabled: Boolean) {
        viewModelScope.launch {
            val current = getSettingsUseCase.getDirect()
            val hash = pin?.hashCode()?.toString()
            saveSettingsUseCase(
                current.copy(
                    isPinLocked = isEnabled,
                    pinHash = if (isEnabled) hash else null
                )
            )
        }
    }
}
