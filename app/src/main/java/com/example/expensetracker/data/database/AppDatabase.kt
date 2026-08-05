package com.example.expensetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expensetracker.data.dao.BudgetDao
import com.example.expensetracker.data.dao.CategoryDao
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.dao.PaymentMethodDao
import com.example.expensetracker.data.dao.SettingsDao
import com.example.expensetracker.data.model.BudgetEntity
import com.example.expensetracker.data.model.CategoryEntity
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.data.model.PaymentMethodEntity
import com.example.expensetracker.data.model.SettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        PaymentMethodEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    prepopulateDatabase(database)
                }
            }
        }

        private suspend fun prepopulateDatabase(db: AppDatabase) {
            // Default Categories
            val defaultCategories = listOf(
                CategoryEntity(name = "Food", iconName = "restaurant", colorHex = "#FF9800"),
                CategoryEntity(name = "Travel", iconName = "directions_car", colorHex = "#2196F3"),
                CategoryEntity(name = "Fuel", iconName = "local_gas_station", colorHex = "#00BCD4"),
                CategoryEntity(name = "Shopping", iconName = "shopping_bag", colorHex = "#E91E63"),
                CategoryEntity(name = "Bills", iconName = "receipt_long", colorHex = "#9C27B0"),
                CategoryEntity(name = "Rent", iconName = "home", colorHex = "#795548"),
                CategoryEntity(name = "Groceries", iconName = "shopping_cart", colorHex = "#4CAF50"),
                CategoryEntity(name = "Medical", iconName = "medical_services", colorHex = "#F44336"),
                CategoryEntity(name = "Insurance", iconName = "shield", colorHex = "#607D8B"),
                CategoryEntity(name = "Education", iconName = "school", colorHex = "#3F51B5"),
                CategoryEntity(name = "Entertainment", iconName = "sports_esports", colorHex = "#FFC107"),
                CategoryEntity(name = "Subscriptions", iconName = "card_membership", colorHex = "#673AB7"),
                CategoryEntity(name = "Salary", iconName = "payments", colorHex = "#009688"),
                CategoryEntity(name = "Freelancing", iconName = "work", colorHex = "#8BC34A"),
                CategoryEntity(name = "Investment", iconName = "trending_up", colorHex = "#00E676"),
                CategoryEntity(name = "Gift", iconName = "card_giftcard", colorHex = "#FF4081"),
                CategoryEntity(name = "Charity", iconName = "favorite", colorHex = "#FF5722"),
                CategoryEntity(name = "EMI", iconName = "account_balance", colorHex = "#E64A19"),
                CategoryEntity(name = "Others", iconName = "category", colorHex = "#9E9E9E")
            )
            db.categoryDao().insertCategories(defaultCategories)

            // Default Payment Methods
            val defaultPaymentMethods = listOf(
                PaymentMethodEntity(name = "Google Pay (GPay)", iconName = "qr_code"),
                PaymentMethodEntity(name = "Cash", iconName = "money")
            )
            db.paymentMethodDao().insertPaymentMethods(defaultPaymentMethods)

            // Default Settings Record
            db.settingsDao().insertOrUpdateSettings(
                SettingsEntity(
                    id = 1,
                    isDarkMode = null, // Follow system
                    currencyCode = "INR",
                    currencySymbol = "₹",
                    decimalPrecision = 2,
                    dateFormat = "dd MMM yyyy",
                    timeFormat = "hh:mm a",
                    isPinLocked = false,
                    pinHash = null
                )
            )
        }
    }
}
