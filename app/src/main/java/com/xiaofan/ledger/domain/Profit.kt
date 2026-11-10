package com.xiaofan.ledger.domain

/**
 * 核心计算口径（唯一真源，金额单位：分）。
 *
 * 总成本 = 进货总价 - 总返利 + 快递费
 * 毛利润 = 实际卖价 - 总成本
 *
 * 说明：
 * - 返利视为降低进货成本，因此从成本中扣除；
 * - 无返利 / 无运费时对应值按 0 传入；
 * - 盒损不单独建字段，录入时直接把“实际卖价”改成扣减后的金额即可。
 */
object Profit {

    fun totalCostCents(
        costPriceCents: Long,
        rebateCents: Long,
        shippingFeeCents: Long,
    ): Long = costPriceCents - rebateCents + shippingFeeCents

    fun grossProfitCents(
        actualSellPriceCents: Long,
        costPriceCents: Long,
        rebateCents: Long,
        shippingFeeCents: Long,
    ): Long = actualSellPriceCents - totalCostCents(costPriceCents, rebateCents, shippingFeeCents)
}
