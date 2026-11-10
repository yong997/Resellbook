package com.xiaofan.ledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xiaofan.ledger.domain.Profit

/**
 * 订单实体。金额字段统一以“分”(Long) 存储，杜绝浮点误差。
 *
 * 注意口径：costPriceCents / rebateCents / actualSellPriceCents 都是**单价（每件）**，
 * shippingFeeCents 是**整单快递费总额**。总成本 / 毛利润由数量参与计算（见下方 computed 属性）。
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val quantity: Int,
    /** 进货单价（分/件） */
    val costPriceCents: Long,
    /** 单件返利（分/件） */
    val rebateCents: Long = 0L,
    /** 快递费总额（分/单） */
    val shippingFeeCents: Long = 0L,
    /** 实际卖价单价（分/件） */
    val actualSellPriceCents: Long,
    /** 快递单号：填写即代表已寄出；为空表示未寄出 */
    val trackingNumber: String? = null,
    /** 0 = 未结, 1 = 已结 */
    val status: Int = STATUS_UNSETTLED,
    /** 交易日期：本地零点 epoch millis */
    val date: Long,
    val remark: String = "",
) {
    /** 总卖价 = 实际卖价单价 × 数量 */
    val totalSellPriceCents: Long
        get() = Profit.totalSellCents(actualSellPriceCents, quantity)

    /** 总成本 = (进货单价 - 单件返利) × 数量 + 快递费 */
    val totalCostCents: Long
        get() = Profit.totalCostCents(costPriceCents, rebateCents, shippingFeeCents, quantity)

    /** 毛利润 = 总卖价 - 总成本 */
    val grossProfitCents: Long
        get() = Profit.grossProfitCents(
            actualSellPriceCents, costPriceCents, rebateCents, shippingFeeCents, quantity
        )

    /** 是否已寄出：有快递单号即视为已寄出 */
    val isShipped: Boolean get() = !trackingNumber.isNullOrBlank()

    val isSettled: Boolean get() = status == STATUS_SETTLED

    companion object {
        const val STATUS_UNSETTLED = 0
        const val STATUS_SETTLED = 1
    }
}
