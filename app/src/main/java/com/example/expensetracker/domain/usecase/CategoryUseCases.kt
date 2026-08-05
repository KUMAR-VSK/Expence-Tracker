package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.getAllCategories().map { list ->
            // Sort categories: put pinned ones first, then custom, then alphabetically
            list.sortedWith(
                compareByDescending<Category> { it.isPinned }
                    .thenByDescending { it.isCustom }
                    .thenBy { it.name.lowercase() }
            )
        }
    }
}

class AddCategoryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(category: Category): Result<Long> {
        if (category.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Category name cannot be empty"))
        }
        return try {
            val id = repository.insertCategory(category)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateCategoryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        if (category.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Category name cannot be empty"))
        }
        return try {
            repository.updateCategory(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteCategoryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.deleteCategory(category)
    }
}
