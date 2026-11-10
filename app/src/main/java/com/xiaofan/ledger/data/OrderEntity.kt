package com.xiaofan.ledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xiaofan.ledger.domain.Profit

/**
 * 订单实体。金额字段统一以“分”(Long) 存储，杜绝浮点误差。
 * 对应需求字段：商品名称/数量/进货总价/总返利/快递费/实际卖价/结算状态/交易日期/备注。
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val quantity: Int,
    val costPriceCents: Long,
    val rebateCents: Long = 0L,
    val shippingFeeCents: Long = 0L,
    val actualSellPriceCents: Long,
    /** 0 = 未结, 1 = 已结 */
    val status: Int = STATUS_UNSETTLED,
    /** 交易日期：本地零点 epoch millis */
    val date: Long,
    val remark: String = "",
) {
    /** 总成本 = 进货总价 - 总返利 + 快递费 */
    val totalCostCents: Long
        get() = Profit.totalCostCents(costPriceCents, rebateCents, shippingFeeCents)

    /** 毛利润 = 实际卖价 - 总成本 */
    val grossProfitCents: Long
        get() = Profit.grossProfitCents(
            actualSellPriceCents, costPriceCents, rebateCents, shippingFeeCents
        )

    val isSettled: Boolean get() = status == STATUS_SETTLED

    companion object {
        const val STATUS_UNSETTLED = 0
        const val STATUS_SETTLED = 1
    }
}
