package com.xiaofan.ledger

import com.xiaofan.ledger.util.Money
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyTest {

    @Test
    fun parseToCents() {
        assertEquals(1_235L, Money.parseYuanToCents("12.345"))
        assertEquals(1_200L, Money.parseYuanToCents("12"))
        assertEquals(1_234_560L, Money.parseYuanToCents("12,345.6"))
        assertEquals(0L, Money.parseYuanToCents(""))
        assertEquals(0L, Money.parseYuanToCents("   "))
        assertEquals(0L, Money.parseYuanToCents("0"))
        assertNull(Money.parseYuanToCents("abc"))
        assertNull(Money.parseYuanToCents("-5"))
    }

    @Test
    fun formatFromCents() {
        assertEquals("12.35", Money.formatCents(1_235L))
        assertEquals("¥12.35", Money.formatYuan(1_235L))
        assertEquals("1,234.56", Money.formatCents(123_456L))
        assertEquals("12.30", Money.centsToPlainString(1_230L))
    }
}
