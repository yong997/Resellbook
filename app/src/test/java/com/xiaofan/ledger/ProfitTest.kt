package com.xiaofan.ledger

import com.xiaofan.ledger.domain.Profit
import org.junit.Assert.assertEquals
import org.junit.Test

/** 核心公式单元测试（金额单位：分）。 */
class ProfitTest {

    @Test
    fun noRebateAndNoShipping() {
        // 进货 100.00，无返利/运费，卖 120.00
        assertEquals(10_000L, Profit.totalCostCents(10_000L, 0L, 0L))
        assertEquals(2_000L, Profit.grossProfitCents(12_000L, 10_000L, 0L, 0L))
    }

    @Test
    fun withRebateAndShipping() {
        // 进货 100.00，返利 10.00，运费 5.00 -> 总成本 95.00；卖 120.00 -> 利润 25.00
        assertEquals(9_500L, Profit.totalCostCents(10_000L, 1_000L, 500L))
        assertEquals(2_500L, Profit.grossProfitCents(12_000L, 10_000L, 1_000L, 500L))
    }

    @Test
    fun boxDamageLowerSellPrice() {
        // 进货 100.00，盒损后实际卖价 88.00 -> 利润 -12.00
        assertEquals(10_000L, Profit.totalCostCents(10_000L, 0L, 0L))
        assertEquals(-1_200L, Profit.grossProfitCents(8_800L, 10_000L, 0L, 0L))
    }

    @Test
    fun rebateCanExceedCost() {
        // 返利高于进货价：总成本为负，利润相应提高
        assertEquals(-2_000L, Profit.totalCostCents(10_000L, 12_000L, 0L))
        assertEquals(14_000L, Profit.grossProfitCents(12_000L, 10_000L, 12_000L, 0L))
    }
}
