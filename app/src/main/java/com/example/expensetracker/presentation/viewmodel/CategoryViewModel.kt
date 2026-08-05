package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.usecase.AddCategoryUseCase
import com.example.expensetracker.domain.usecase.DeleteCategoryUseCase
import com.example.expensetracker.domain.usecase.GetCategoriesUseCase
import com.example.expensetracker.domain.usecase.UpdateCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryFormState(
    val name: String = "",
    val iconName: String = "category",
    val colorHex: String = "#FF9800",
    val nameError: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _formState = MutableStateFlow(CategoryFormState())
    val formState: StateFlow<CategoryFormState> = _formState.asStateFlow()

    val categories: StateFlow<List<Category>> = getCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onNameChange(value: String) {
        _formState.update { it.copy(name = value, nameError = null) }
    }

    fun onIconChange(icon: String) {
        _formState.update { it.copy(iconName = icon) }
    }

    fun onColorChange(color: String) {
        _formState.update { it.copy(colorHex = color) }
    }

    fun resetForm() {
        _formState.value = CategoryFormState()
    }

    fun saveCategory() {
        val state = _formState.value
        if (state.name.isBlank()) {
            _formState.update { it.copy(nameError = "Category name cannot be empty") }
            return
        }

        val category = Category(
            name = state.name.trim(),
            iconName = state.iconName,
            colorHex = state.colorHex,
            isCustom = true,
            isPinned = false
        )

        viewModelScope.launch {
            val result = addCategoryUseCase(category)
            if (result.isSuccess) {
                _formState.update { it.copy(saveSuccess = true) }
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category)
        }
    }

    fun togglePinned(category: Category) {
        viewModelScope.launch {
            updateCategoryUseCase(category.copy(isPinned = !category.isPinned))
        }
    }
}
