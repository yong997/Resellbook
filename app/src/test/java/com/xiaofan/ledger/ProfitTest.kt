package com.xiaofan.ledger

import com.xiaofan.ledger.domain.Profit
import org.junit.Assert.assertEquals
import org.junit.Test

/** 核心公式单元测试（金额单位：分；进货/返利/卖价均为单价，数量参与计算）。 */
class ProfitTest {

    @Test
    fun quantityMultipliesProfit_userCase() {
        // 商品A：单价进货 80、单件返利 12、单价卖 74、数量 8
        // 单件成本 = 80 - 12 = 68；单件利润 = 74 - 68 = 6；毛利润 = 6 × 8 = 48
        assertEquals(54_400L, Profit.totalCostCents(8_000L, 1_200L, 0L, 8))
        assertEquals(59_200L, Profit.totalSellCents(7_400L, 8))
        assertEquals(4_800L, Profit.grossProfitCents(7_400L, 8_000L, 1_200L, 0L, 8))
    }

    @Test
    fun quantityWithShipping() {
        // (80-12)*8 + 20 运费 = 564；卖价 74*8 = 592；毛利润 28
        assertEquals(56_400L, Profit.totalCostCents(8_000L, 1_200L, 2_000L, 8))
        assertEquals(59_200L, Profit.totalSellCents(7_400L, 8))
        assertEquals(2_800L, Profit.grossProfitCents(7_400L, 8_000L, 1_200L, 2_000L, 8))
    }

    @Test
    fun singleQuantityNoRebateNoShipping() {
        assertEquals(10_000L, Profit.totalCostCents(10_000L, 0L, 0L, 1))
        assertEquals(12_000L, Profit.totalSellCents(12_000L, 1))
        assertEquals(2_000L, Profit.grossProfitCents(12_000L, 10_000L, 0L, 0L, 1))
    }

    @Test
    fun boxDamageLowerSellPrice() {
        // 进货 100、卖 88、数量 1 -> 利润 -12
        assertEquals(10_000L, Profit.totalCostCents(10_000L, 0L, 0L, 1))
        assertEquals(-1_200L, Profit.grossProfitCents(8_800L, 10_000L, 0L, 0L, 1))
    }

    @Test
    fun rebateCanExceedCost() {
        // 单件返利高于进货单价：单件成本为负，利润相应提高
        assertEquals(-2_000L, Profit.totalCostCents(10_000L, 12_000L, 0L, 1))
        assertEquals(14_000L, Profit.grossProfitCents(12_000L, 10_000L, 12_000L, 0L, 1))
    }
}
