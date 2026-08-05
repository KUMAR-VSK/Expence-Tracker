package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

enum class SortOrder {
    NEWEST, OLDEST, HIGHEST, LOWEST
}

class GetExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(
        query: String = "",
        categoryIds: Set<Long> = emptySet(),
        paymentMethodIds: Set<Long> = emptySet(),
        types: Set<TransactionType> = emptySet(),
        startDate: Long? = null,
        endDate: Long? = null,
        minAmount: Double? = null,
        maxAmount: Double? = null,
        sortBy: SortOrder = SortOrder.NEWEST
    ): Flow<List<Expense>> {
        return repository.getAllExpenses().map { list ->
            var filtered = list

            // Query search (notes or tags)
            if (query.isNotBlank()) {
                filtered = filtered.filter { expense ->
                    expense.notes.contains(query, ignoreCase = true) ||
                    expense.tags.any { tag -> tag.contains(query, ignoreCase = true) } ||
                    (expense.category?.name?.contains(query, ignoreCase = true) == true)
                }
            }

            // Category filter
            if (categoryIds.isNotEmpty()) {
                filtered = filtered.filter { it.category?.id in categoryIds }
            }

            // Payment method filter
            if (paymentMethodIds.isNotEmpty()) {
                filtered = filtered.filter { it.paymentMethod?.id in paymentMethodIds }
            }

            // Types filter
            if (types.isNotEmpty()) {
                filtered = filtered.filter { it.type in types }
            }

            // Date range filter
            if (startDate != null) {
                filtered = filtered.filter { it.dateLong >= startDate }
            }
            if (endDate != null) {
                filtered = filtered.filter { it.dateLong <= endDate }
            }

            // Amount range filter
            if (minAmount != null) {
                filtered = filtered.filter { it.amount >= minAmount }
            }
            if (maxAmount != null) {
                filtered = filtered.filter { it.amount <= maxAmount }
            }

            // Sorting
            filtered = when (sortBy) {
                SortOrder.NEWEST -> filtered.sortedWith(compareByDescending<Expense> { it.dateLong }.thenByDescending { it.id })
                SortOrder.OLDEST -> filtered.sortedWith(compareBy<Expense> { it.dateLong }.thenBy { it.id })
                SortOrder.HIGHEST -> filtered.sortedByDescending { it.amount }
                SortOrder.LOWEST -> filtered.sortedBy { it.amount }
            }

            filtered
        }
    }
}

class GetExpenseByIdUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: Long): Expense? {
        return repository.getExpenseById(id)
    }
}

class AddExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Long> {
        if (expense.amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be positive"))
        }
        if (expense.category == null) {
            return Result.failure(IllegalArgumentException("Category cannot be empty"))
        }
        return try {
            val id = repository.insertExpense(expense)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Result<Unit> {
        if (expense.amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be positive"))
        }
        if (expense.category == null) {
            return Result.failure(IllegalArgumentException("Category cannot be empty"))
        }
        return try {
            repository.updateExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        repository.deleteExpense(expense)
    }
}

class DeleteExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(ids: List<Long>) {
        repository.deleteExpenses(ids)
    }
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: Long, isFavorite: Boolean) {
        repository.toggleFavorite(id, isFavorite)
    }
}
