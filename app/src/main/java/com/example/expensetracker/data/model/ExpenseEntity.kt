package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PaymentMethodEntity::class,
            parentColumns = ["id"],
            childColumns = ["paymentMethodId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val categoryId: Long,
    val dateLong: Long, // Epoch milliseconds for sorting & filtering
    val timeString: String, // HH:mm
    val paymentMethodId: Long,
    val notes: String,
    val location: String? = null,
    val tags: String = "", // Comma-separated list of tags
    val isFavorite: Boolean = false,
    val repeatInterval: String? = null // Default null / "None"
)
