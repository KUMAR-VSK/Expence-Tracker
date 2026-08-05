package com.example.expensetracker.domain.usecase

import com.example.expensetracker.domain.model.PaymentMethod
import com.example.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPaymentMethodsUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<PaymentMethod>> {
        return repository.getAllPaymentMethods().map { list ->
            list.sortedBy { it.name.lowercase() }
        }
    }
}

class AddPaymentMethodUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(paymentMethod: PaymentMethod): Result<Long> {
        if (paymentMethod.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Payment method name cannot be empty"))
        }
        return try {
            val id = repository.insertPaymentMethod(paymentMethod)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdatePaymentMethodUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(paymentMethod: PaymentMethod): Result<Unit> {
        if (paymentMethod.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Payment method name cannot be empty"))
        }
        return try {
            repository.updatePaymentMethod(paymentMethod)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeletePaymentMethodUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(paymentMethod: PaymentMethod) {
        repository.deletePaymentMethod(paymentMethod)
    }
}
