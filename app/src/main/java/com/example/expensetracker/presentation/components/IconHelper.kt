package com.example.expensetracker.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconHelper {

    fun getIconByName(name: String): ImageVector {
        return when (name.lowercase()) {
            "restaurant", "food", "dining" -> Icons.Outlined.Restaurant
            "directions_car", "travel", "car" -> Icons.Outlined.DirectionsCar
            "local_gas_station", "fuel", "gas" -> Icons.Outlined.LocalGasStation
            "shopping_bag", "shopping", "bag" -> Icons.Outlined.ShoppingBag
            "receipt_long", "bills", "bill" -> Icons.Outlined.ReceiptLong
            "home", "house", "rent" -> Icons.Outlined.Home
            "shopping_cart", "groceries", "cart" -> Icons.Outlined.ShoppingCart
            "medical_services", "medical", "doctor" -> Icons.Outlined.MedicalServices
            "shield", "insurance" -> Icons.Outlined.Shield
            "school", "education", "book" -> Icons.Outlined.School
            "sports_esports", "entertainment", "game" -> Icons.Outlined.SportsEsports
            "card_membership", "subscriptions", "sub" -> Icons.Outlined.CardMembership
            "payments", "salary", "cash" -> Icons.Outlined.Payments
            "work", "freelancing", "job" -> Icons.Outlined.Work
            "trending_up", "investment", "stocks" -> Icons.Outlined.TrendingUp
            "card_giftcard", "gift" -> Icons.Outlined.CardGiftcard
            "favorite", "charity", "love" -> Icons.Outlined.FavoriteBorder
            "account_balance", "emi", "bank" -> Icons.Outlined.AccountBalance
            
            // Payment methods
            "money" -> Icons.Outlined.AttachMoney
            "qr_code", "upi" -> Icons.Outlined.QrCode
            "credit_card", "card", "debit_card" -> Icons.Outlined.CreditCard
            "account_balance_wallet", "wallet" -> Icons.Outlined.AccountBalanceWallet
            "payment" -> Icons.Outlined.Payment
            
            // Fallbacks & standard categories
            "pets" -> Icons.Outlined.Pets
            "face", "kids" -> Icons.Outlined.Face
            "flight", "vacation" -> Icons.Outlined.Flight
            "spa", "personal_care" -> Icons.Outlined.Spa
            "fitness_center", "gym" -> Icons.Outlined.FitnessCenter
            "computer", "electronics" -> Icons.Outlined.Computer
            "checkroom", "clothing" -> Icons.Outlined.Checkroom
            
            else -> Icons.Outlined.Category
        }
    }

    val availableCategoryIcons = listOf(
        "restaurant", "directions_car", "local_gas_station", "shopping_bag", 
        "receipt_long", "home", "shopping_cart", "medical_services", 
        "shield", "school", "sports_esports", "card_membership", 
        "payments", "work", "trending_up", "card_giftcard", 
        "favorite", "account_balance", "pets", "face", 
        "flight", "spa", "fitness_center", "computer", "checkroom"
    )

    val availablePaymentIcons = listOf(
        "money", "qr_code", "credit_card", "account_balance_wallet", "payment"
    )
}
