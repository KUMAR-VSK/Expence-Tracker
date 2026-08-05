package com.example.expensetracker.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object History : Screen("history")
    object Analytics : Screen("analytics")
    object Budget : Screen("budget")
    object Settings : Screen("settings")
    
    object AddEditTransaction : Screen("add_edit_transaction?transactionId={transactionId}") {
        fun createRoute(transactionId: Long? = null): String {
            return if (transactionId != null) "add_edit_transaction?transactionId=$transactionId"
            else "add_edit_transaction"
        }
    }
    
    object TransactionDetails : Screen("transaction_details/{transactionId}") {
        fun createRoute(transactionId: Long): String {
            return "transaction_details/$transactionId"
        }
    }
    
    object Reports : Screen("reports")
    object Categories : Screen("categories")
    object PaymentMethods : Screen("payment_methods")
    object Profile : Screen("profile")
    object About : Screen("about")
    object SecurityLock : Screen("security_lock")
}
