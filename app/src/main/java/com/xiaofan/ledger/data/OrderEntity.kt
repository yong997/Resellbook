package com.xiaofan.ledger.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xiaofan.ledger.domain.Profit

/**
 * 订单实体。金额字段统一以“分”(Long) 存储，杜绝浮点误差。
 *
 * 口径：costPriceCents / rebateCents / actualSellPriceCents 都是**单价（每件）**，
 * shippingFeeCents 是**整单快递费总额**。总成本 / 毛利润由数量参与计算。
 *
 * 返利来源：
 * - 自己账号（默认）：必然到账，不追踪；
 * - 他人代返：返利在对方处，需要你主动追讨，用 rebateReturned 标记是否已返还。
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
    /** 返利来源：0=自己账号(自动到账) 1=他人代返(需追讨) */
    @ColumnInfo(defaultValue = "0") val rebateSource: Int = REBATE_SOURCE_SELF,
    /** 他人代返时：对方是否已返还 */
    @ColumnInfo(defaultValue = "0") val rebateReturned: Boolean = false,
    /** 他人代返时：对方称呼（可选） */
    val counterparty: String? = null,
    /** 他人代返时：约定返还日期（本地零点毫秒，可选） */
    val rebateDueDate: Long? = null,
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

    /** 毛利润（含返利的预期毛利） = 总卖价 - 总成本 */
    val grossProfitCents: Long
        get() = Profit.grossProfitCents(
            actualSellPriceCents, costPriceCents, rebateCents, shippingFeeCents, quantity
        )

    /** 是否已寄出：有快递单号即视为已寄出 */
    val isShipped: Boolean get() = !trackingNumber.isNullOrBlank()

    /** 是否有一笔“待收返利”：他人代返且尚未返还 */
    val isRebatePending: Boolean
        get() = rebateSource == REBATE_SOURCE_OTHER && !rebateReturned && rebateCents > 0L && quantity > 0

    /** 待收返利金额（分） = 单件返利 × 数量；非待收则为 0 */
    val pendingRebateCents: Long
        get() = if (isRebatePending) rebateCents * quantity else 0L

    val isSettled: Boolean get() = status == STATUS_SETTLED

    companion object {
        const val STATUS_UNSETTLED = 0
        const val STATUS_SETTLED = 1

        const val REBATE_SOURCE_SELF = 0
        const val REBATE_SOURCE_OTHER = 1
    }
}
