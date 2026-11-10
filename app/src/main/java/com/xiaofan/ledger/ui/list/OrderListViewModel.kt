package com.xiaofan.ledger.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xiaofan.ledger.ResellbookApp
import com.xiaofan.ledger.data.OrderEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ListSummary(
    val totalProfitCents: Long = 0L,
    val pendingReceivableCents: Long = 0L,
    val orderCount: Int = 0,
    val unsettledCount: Int = 0,
)

data class OrderListUiState(
    val orders: List<OrderEntity> = emptyList(),
    val summary: ListSummary = ListSummary(),
    val loading: Boolean = true,
)

class OrderListViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = (app as ResellbookApp).repository

    val uiState: StateFlow<OrderListUiState> = repository.observeOrders()
        .map { list ->
            OrderListUiState(
                orders = list,
                summary = ListSummary(
                    totalProfitCents = list.sumOf { it.grossProfitCents },
                    pendingReceivableCents = list.filter { !it.isSettled }
                        .sumOf { it.totalSellPriceCents },
                    orderCount = list.size,
                    unsettledCount = list.count { !it.isSettled },
                ),
                loading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OrderListUiState(),
        )

    fun toggleStatus(order: OrderEntity) = viewModelScope.launch {
        repository.toggleStatus(order.id, order.status)
    }

    fun delete(order: OrderEntity) = viewModelScope.launch {
        repository.delete(order.id)
    }
}
