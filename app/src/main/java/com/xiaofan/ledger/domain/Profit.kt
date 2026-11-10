package com.xiaofan.ledger.domain

/**
 * 核心计算口径（唯一真源，金额单位：分；进货价/返利/卖价均为“单价/每件”）。
 *
 * 总成本 = (进货单价 - 单件返利) × 数量 + 快递费
 * 总卖价 = 实际卖价单价 × 数量
 * 毛利润 = 总卖价 - 总成本
 *
 * 说明：
 * - 返利视为降低进货成本，按“每件返利”计算，先扣减单价再乘数量；
 * - 快递费是整单总额，只加一次；
 * - 无返利 / 无运费时对应值按 0 传入；
 * - 盒损不单独建字段，录入时直接把“实际卖价单价”改成扣减后的金额即可。
 */
object Profit {

    fun totalSellCents(actualSellPriceUnitCents: Long, quantity: Int): Long =
        actualSellPriceUnitCents * quantity

    fun totalCostCents(
        costPriceUnitCents: Long,
        rebateUnitCents: Long,
        shippingFeeCents: Long,
        quantity: Int,
    ): Long = (costPriceUnitCents - rebateUnitCents) * quantity + shippingFeeCents

    fun grossProfitCents(
        actualSellPriceUnitCents: Long,
        costPriceUnitCents: Long,
        rebateUnitCents: Long,
        shippingFeeCents: Long,
        quantity: Int,
    ): Long =
        totalSellCents(actualSellPriceUnitCents, quantity) -
            totalCostCents(costPriceUnitCents, rebateUnitCents, shippingFeeCents, quantity)
}
