package com.example.expensetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.domain.model.Category
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.PaymentMethod
import com.example.expensetracker.domain.model.TransactionType
import com.example.expensetracker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionFormState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedCategory: Category? = null,
    val dateLong: Long = System.currentTimeMillis(),
    val timeString: String = "12:00 PM",
    val selectedPaymentMethod: PaymentMethod? = null,
    val notes: String = "",
    val location: String? = null,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val repeatInterval: String? = null,
    val amountError: String? = null,
    val categoryError: String? = null,
    val paymentError: String? = null,
    val saveSuccess: Boolean = false
)

data class HistoryFilterState(
    val query: String = "",
    val selectedCategoryIds: Set<Long> = emptySet(),
    val selectedPaymentIds: Set<Long> = emptySet(),
    val selectedTypes: Set<TransactionType> = emptySet(),
    val startDate: Long? = null,
    val endDate: Long? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val sortBy: SortOrder = SortOrder.NEWEST
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val deleteExpensesUseCase: DeleteExpensesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase
) : ViewModel() {

    // Form states
    private val _formState = MutableStateFlow(TransactionFormState())
    val formState: StateFlow<TransactionFormState> = _formState.asStateFlow()

    // Filters for history
    private val _filterState = MutableStateFlow(HistoryFilterState())
    val filterState: StateFlow<HistoryFilterState> = _filterState.asStateFlow()

    val categories: StateFlow<List<Category>> = getCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentMethods: StateFlow<List<PaymentMethod>> = getPaymentMethodsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactive list of expenses
    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredExpenses: StateFlow<List<Expense>> = _filterState.flatMapLatest { fs ->
        getExpensesUseCase(
            query = fs.query,
            categoryIds = fs.selectedCategoryIds,
            paymentMethodIds = fs.selectedPaymentIds,
            types = fs.selectedTypes,
            startDate = fs.startDate,
            endDate = fs.endDate,
            minAmount = fs.minAmount,
            maxAmount = fs.maxAmount,
            sortBy = fs.sortBy
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Form Actions ---

    fun onAmountChange(value: String) {
        _formState.update { it.copy(amount = value, amountError = null) }
    }

    fun onTypeChange(type: TransactionType) {
        _formState.update { it.copy(type = type) }
    }

    fun onCategorySelect(category: Category) {
        _formState.update { it.copy(selectedCategory = category, categoryError = null) }
    }

    fun onPaymentSelect(method: PaymentMethod) {
        _formState.update { it.copy(selectedPaymentMethod = method, paymentError = null) }
    }

    fun onDateChange(dateMs: Long) {
        _formState.update { it.copy(dateLong = dateMs) }
    }

    fun onTimeChange(timeStr: String) {
        _formState.update { it.copy(timeString = timeStr) }
    }

    fun onNotesChange(notes: String) {
        _formState.update { it.copy(notes = notes) }
    }

    fun onLocationChange(loc: String?) {
        _formState.update { it.copy(location = loc) }
    }

    fun onTagsChange(tags: List<String>) {
        _formState.update { it.copy(tags = tags) }
    }

    fun onFavoriteToggleForm(isFav: Boolean) {
        _formState.update { it.copy(isFavorite = isFav) }
    }

    fun resetForm() {
        _formState.value = TransactionFormState()
    }

    fun loadFormFromExpense(id: Long) {
        viewModelScope.launch {
            val expense = getExpenseByIdUseCase(id)
            if (expense != null) {
                _formState.value = TransactionFormState(
                    amount = expense.amount.toString(),
                    type = expense.type,
                    selectedCategory = expense.category,
                    dateLong = expense.dateLong,
                    timeString = expense.timeString,
                    selectedPaymentMethod = expense.paymentMethod,
                    notes = expense.notes,
                    location = expense.location,
                    tags = expense.tags,
                    isFavorite = expense.isFavorite,
                    repeatInterval = expense.repeatInterval
                )
            }
        }
    }

    fun saveTransaction(existingId: Long?) {
        val state = _formState.value
        val amountVal = state.amount.toDoubleOrNull()
        
        var hasError = false
        if (amountVal == null || amountVal <= 0) {
            _formState.update { it.copy(amountError = "Enter a valid positive amount") }
            hasError = true
        }
        if (state.selectedCategory == null) {
            _formState.update { it.copy(categoryError = "Please select a category") }
            hasError = true
        }
        if (state.selectedPaymentMethod == null) {
            _formState.update { it.copy(paymentError = "Please select a payment method") }
            hasError = true
        }

        if (hasError) return

        val expense = Expense(
            id = existingId ?: 0L,
            amount = amountVal!!,
            type = state.type,
            category = state.selectedCategory,
            dateLong = state.dateLong,
            timeString = state.timeString,
            paymentMethod = state.selectedPaymentMethod,
            notes = state.notes,
            location = state.location,
            tags = state.tags,
            isFavorite = state.isFavorite,
            repeatInterval = state.repeatInterval
        )

        viewModelScope.launch {
            val result = if (existingId != null && existingId > 0) {
                updateExpenseUseCase(expense)
            } else {
                addExpenseUseCase(expense).map { Unit }
            }
            
            if (result.isSuccess) {
                _formState.update { it.copy(saveSuccess = true) }
            }
        }
    }

    fun duplicateTransaction(expense: Expense) {
        viewModelScope.launch {
            val duplicated = expense.copy(id = 0L, isFavorite = false)
            addExpenseUseCase(duplicated)
        }
    }

    fun deleteTransaction(expense: Expense) {
        viewModelScope.launch {
            deleteExpenseUseCase(expense)
        }
    }

    fun deleteTransactions(ids: List<Long>) {
        viewModelScope.launch {
            deleteExpensesUseCase(ids)
        }
    }

    fun toggleFavorite(expense: Expense) {
        viewModelScope.launch {
            toggleFavoriteUseCase(expense.id, !expense.isFavorite)
        }
    }

    // --- Filter Actions ---

    fun onQueryChange(q: String) {
        _filterState.update { it.copy(query = q) }
    }

    fun toggleCategoryFilter(catId: Long) {
        _filterState.update { state ->
            val set = state.selectedCategoryIds.toMutableSet()
            if (set.contains(catId)) set.remove(catId) else set.add(catId)
            state.copy(selectedCategoryIds = set)
        }
    }

    fun togglePaymentFilter(pmId: Long) {
        _filterState.update { state ->
            val set = state.selectedPaymentIds.toMutableSet()
            if (set.contains(pmId)) set.remove(pmId) else set.add(pmId)
            state.copy(selectedPaymentIds = set)
        }
    }

    fun toggleTypeFilter(type: TransactionType) {
        _filterState.update { state ->
            val set = state.selectedTypes.toMutableSet()
            if (set.contains(type)) set.remove(type) else set.add(type)
            state.copy(selectedTypes = set)
        }
    }

    fun onDateRangeChange(start: Long?, end: Long?) {
        _filterState.update { it.copy(startDate = start, endDate = end) }
    }

    fun onAmountRangeChange(min: Double?, max: Double?) {
        _filterState.update { it.copy(minAmount = min, maxAmount = max) }
    }

    fun onSortChange(sort: SortOrder) {
        _filterState.update { it.copy(sortBy = sort) }
    }

    fun resetFilters() {
        _filterState.value = HistoryFilterState()
    }
}
