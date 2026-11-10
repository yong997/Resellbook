package com.xiaofan.ledger.ui.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.xiaofan.ledger.ResellbookApp
import com.xiaofan.ledger.data.OrderEntity
import com.xiaofan.ledger.domain.Profit
import com.xiaofan.ledger.util.Dates
import com.xiaofan.ledger.util.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditFormState(
    val id: Long = 0L,
    val isNew: Boolean = true,
    val loaded: Boolean = false,
    val title: String = "",
    val quantity: String = "1",
    /** 进货单价（元/件） */
    val costPrice: String = "",
    /** 单件返利（元/件） */
    val rebate: String = "",
    /** 快递费总额（元/单） */
    val shipping: String = "",
    /** 实际卖价单价（元/件） */
    val actualSellPrice: String = "",
    /** 快递单号（填写即代表已寄出） */
    val trackingNumber: String = "",
    val date: Long = Dates.todayStart(),
    val remark: String = "",
    val settled: Boolean = false,
    // 行内错误
    val titleError: String? = null,
    val quantityError: String? = null,
    val costPriceError: String? = null,
    val rebateError: String? = null,
    val shippingError: String? = null,
    val actualSellPriceError: String? = null,
) {
    val quantityInt: Int get() = quantity.toIntOrNull()?.coerceAtLeast(0) ?: 0

    val costPriceCents: Long get() = Money.parseYuanToCents(costPrice) ?: 0L
    val rebateCents: Long get() = Money.parseYuanToCents(rebate) ?: 0L
    val shippingCents: Long get() = Money.parseYuanToCents(shipping) ?: 0L
    val actualSellCents: Long get() = Money.parseYuanToCents(actualSellPrice) ?: 0L

    val totalCostCents: Long
        get() = Profit.totalCostCents(costPriceCents, rebateCents, shippingCents, quantityInt)

    val totalSellCents: Long
        get() = Profit.totalSellCents(actualSellCents, quantityInt)

    val grossProfitCents: Long
        get() = Profit.grossProfitCents(
            actualSellCents, costPriceCents, rebateCents, shippingCents, quantityInt
        )
}

class OrderEditViewModel(
    app: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(app) {

    private val repository = (app as ResellbookApp).repository
    private val orderId: Long = savedStateHandle.get<Long>("orderId") ?: -1L

    private val _state = MutableStateFlow(EditFormState(isNew = orderId <= 0L))
    val state: StateFlow<EditFormState> = _state.asStateFlow()

    init {
        if (orderId > 0L) {
            load(orderId)
        } else {
            _state.update { it.copy(loaded = true) }
        }
    }

    private fun load(id: Long) {
        viewModelScope.launch {
            val order = repository.get(id)
            if (order == null) {
                _state.update { it.copy(loaded = true) }
                return@launch
            }
            _state.value = EditFormState(
                id = order.id,
                isNew = false,
                loaded = true,
                title = order.title,
                quantity = order.quantity.toString(),
                costPrice = Money.centsToPlainString(order.costPriceCents),
                rebate = Money.centsToPlainString(order.rebateCents),
                shipping = Money.centsToPlainString(order.shippingFeeCents),
                actualSellPrice = Money.centsToPlainString(order.actualSellPriceCents),
                trackingNumber = order.trackingNumber ?: "",
                date = order.date,
                remark = order.remark,
                settled = order.isSettled,
            )
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value, titleError = null) }
    fun onQuantityChange(value: String) =
        _state.update { it.copy(quantity = value, quantityError = null) }

    fun onCostPriceChange(value: String) =
        _state.update { it.copy(costPrice = value, costPriceError = null) }

    fun onRebateChange(value: String) =
        _state.update { it.copy(rebate = value, rebateError = null) }

    fun onShippingChange(value: String) =
        _state.update { it.copy(shipping = value, shippingError = null) }

    fun onActualSellPriceChange(value: String) =
        _state.update { it.copy(actualSellPrice = value, actualSellPriceError = null) }

    fun onTrackingNumberChange(value: String) =
        _state.update { it.copy(trackingNumber = value) }

    fun onDateChange(value: Long) = _state.update { it.copy(date = value) }
    fun onRemarkChange(value: String) = _state.update { it.copy(remark = value) }
    fun onSettledChange(value: Boolean) = _state.update { it.copy(settled = value) }

    /** 校验并保存；成功回调 onSaved。 */
    fun save(onSaved: () -> Unit) {
        val s = _state.value

        val titleError = if (s.title.isBlank()) "请输入商品名称" else null
        val quantity = s.quantity.toIntOrNull()
        val quantityError = if (quantity == null || quantity <= 0) "数量需为正整数" else null
        val costPriceError = validateMoney(s.costPrice)
        val rebateError = validateMoney(s.rebate)
        val shippingError = validateMoney(s.shipping)
        val actualSellPriceError = validateMoney(s.actualSellPrice)

        val hasError = listOf(
            titleError, quantityError, costPriceError,
            rebateError, shippingError, actualSellPriceError,
        ).any { it != null }

        if (hasError) {
            _state.update {
                it.copy(
                    titleError = titleError,
                    quantityError = quantityError,
                    costPriceError = costPriceError,
                    rebateError = rebateError,
                    shippingError = shippingError,
                    actualSellPriceError = actualSellPriceError,
                )
            }
            return
        }

        viewModelScope.launch {
            val entity = OrderEntity(
                id = if (s.isNew) 0L else s.id,
                title = s.title.trim(),
                quantity = quantity!!,
                costPriceCents = s.costPriceCents,
                rebateCents = s.rebateCents,
                shippingFeeCents = s.shippingCents,
                actualSellPriceCents = s.actualSellCents,
                trackingNumber = s.trackingNumber.trim().ifBlank { null },
                status = if (s.settled) {
                    OrderEntity.STATUS_SETTLED
                } else {
                    OrderEntity.STATUS_UNSETTLED
                },
                date = s.date,
                remark = s.remark.trim(),
            )
            if (s.isNew) repository.add(entity) else repository.update(entity)
            onSaved()
        }
    }

    private fun validateMoney(value: String): String? {
        if (value.isBlank()) return null
        val cents = Money.parseYuanToCents(value) ?: return "请输入有效金额"
        return if (cents < 0L) "金额不能为负" else null
    }
}
