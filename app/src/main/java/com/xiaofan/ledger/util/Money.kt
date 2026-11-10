package com.xiaofan.ledger.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * 金额工具：内部一律以“分”(Long) 表示，避免任何浮点误差。
 * 仅在展示/录入的边界做“元 <-> 分”的转换。
 */
object Money {

    /** 解析“元”文本到“分”。空串/纯空白 -> 0；非法或负数 -> null。 */
    fun parseYuanToCents(input: String): Long? {
        val raw = input.trim().replace(",", "").replace("¥", "").replace("￥", "")
        if (raw.isEmpty()) return 0L
        val bd = try {
            BigDecimal(raw)
        } catch (_: NumberFormatException) {
            return null
        }
        if (bd.signum() < 0) return null
        return try {
            bd.movePointRight(2).setScale(0, RoundingMode.HALF_UP).toLong()
        } catch (_: ArithmeticException) {
            null
        }
    }

    private val grouping: DecimalFormat =
        DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.CHINA))

    /** “分” -> "1,234.56"（不带货币符号）。 */
    fun formatCents(cents: Long): String =
        grouping.format(BigDecimal.valueOf(cents).movePointLeft(2))

    /** “分” -> "¥1,234.56"。 */
    fun formatYuan(cents: Long): String = "¥" + formatCents(cents)

    /** “分” -> "12.34"（无千分位，用于回填输入框）。 */
    fun centsToPlainString(cents: Long): String =
        BigDecimal.valueOf(cents).movePointLeft(2).setScale(2, RoundingMode.HALF_UP).toPlainString()
}
