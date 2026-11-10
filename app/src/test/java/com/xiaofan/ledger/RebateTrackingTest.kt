package com.xiaofan.ledger

import com.xiaofan.ledger.data.OrderEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** “待收返利”逻辑单元测试（不依赖 Android）。 */
class RebateTrackingTest {

    private fun order(
        source: Int,
        returned: Boolean,
        quantity: Int = 8,
        rebateUnitCents: Long = 1_200L,
    ) = OrderEntity(
        id = 1L,
        title = "A",
        quantity = quantity,
        costPriceCents = 8_000L,
        rebateCents = rebateUnitCents,
        shippingFeeCents = 0L,
        actualSellPriceCents = 7_400L,
        rebateSource = source,
        rebateReturned = returned,
        date = 0L,
    )

    @Test
    fun otherPendingRebate() {
        val o = order(OrderEntity.REBATE_SOURCE_OTHER, false)
        assertTrue(o.isRebatePending)
        assertEquals(9_600L, o.pendingRebateCents) // 单件返利 12.00 × 8 = 96.00
    }

    @Test
    fun otherReturnedNoLongerPending() {
        val o = order(OrderEntity.REBATE_SOURCE_OTHER, true)
        assertFalse(o.isRebatePending)
        assertEquals(0L, o.pendingRebateCents)
    }

    @Test
    fun selfSourceNotTracked() {
        val o = order(OrderEntity.REBATE_SOURCE_SELF, false)
        assertFalse(o.isRebatePending)
        assertEquals(0L, o.pendingRebateCents)
    }

    @Test
    fun profitUnaffectedByTracking() {
        val o = order(OrderEntity.REBATE_SOURCE_OTHER, false)
        // 毛利仍是“含返利的预期毛利” = 48.00，追踪状态不改变利润公式
        assertEquals(4_800L, o.grossProfitCents)
    }

    @Test
    fun zeroRebateNotPending() {
        val o = order(OrderEntity.REBATE_SOURCE_OTHER, false, rebateUnitCents = 0L)
        assertFalse(o.isRebatePending)
        assertEquals(0L, o.pendingRebateCents)
    }
}
