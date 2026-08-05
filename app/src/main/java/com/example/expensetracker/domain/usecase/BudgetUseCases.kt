package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.Budget
import com.example.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Budget>> {
        return repository.getAllBudgets()
    }
}

class AddBudgetUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(budget: Budget): Result<Long> {
        if (budget.amount <= 0) {
            return Result.failure(IllegalArgumentException("Budget amount must be positive"))
        }
        return try {
            val id = repository.insertBudget(budget)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateBudgetUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(budget: Budget): Result<Unit> {
        if (budget.amount <= 0) {
            return Result.failure(IllegalArgumentException("Budget amount must be positive"))
        }
        return try {
            repository.updateBudget(budget)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteBudgetUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(budget: Budget) {
        repository.deleteBudget(budget)
    }
}
