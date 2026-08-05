package com.example.expensetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expensetracker.data.dao.*
import com.example.expensetracker.data.model.*

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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            
            // Direct synchronous SQLite prepopulation
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Food', 'restaurant', '#FF9800', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Travel', 'directions_car', '#2196F3', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Fuel', 'local_gas_station', '#00BCD4', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Shopping', 'shopping_bag', '#E91E63', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Bills', 'receipt_long', '#9C27B0', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Rent', 'home', '#795548', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Groceries', 'shopping_cart', '#4CAF50', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Medical', 'medical_services', '#F44336', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Insurance', 'shield', '#607D8B', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Education', 'school', '#3F51B5', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Entertainment', 'sports_esports', '#FFC107', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Subscriptions', 'card_membership', '#673AB7', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Salary', 'payments', '#009688', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Freelancing', 'work', '#8BC34A', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Investment', 'trending_up', '#00E676', 0, 0)")
            db.execSQL("INSERT INTO categories (name, iconName, colorHex, isCustom, isPinned) VALUES ('Others', 'category', '#9E9E9E', 0, 0)")

            // Default Payment Methods
            db.execSQL("INSERT INTO payment_methods (name, iconName, isCustom) VALUES ('Google Pay', 'qr_code', 0)")
            db.execSQL("INSERT INTO payment_methods (name, iconName, isCustom) VALUES ('Cash', 'money', 0)")

            // Default Settings Record
            db.execSQL("INSERT OR REPLACE INTO settings (id, isDarkMode, currencyCode, currencySymbol, decimalPrecision, dateFormat, timeFormat, isPinLocked, pinHash, userName, savingGoal) VALUES (1, NULL, 'INR', '₹', 2, 'dd MMM yyyy', 'hh:mm a', 0, NULL, 'Local User', 0.0)")
        }
    }
}
