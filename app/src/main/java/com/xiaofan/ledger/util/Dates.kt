package com.xiaofan.ledger.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Dates {

    private val displayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val utc: TimeZone = TimeZone.getTimeZone("UTC")

    fun format(millis: Long): String = displayFormat.format(Date(millis))

    fun todayStart(): Long = startOfDay(System.currentTimeMillis())

    fun startOfDay(millis: Long): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = millis
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    /** 本地日期 -> Material3 DatePicker 需要的 UTC 当日零点毫秒。 */
    fun toPickerMillis(localMillis: Long): Long {
        val local = Calendar.getInstance().apply { timeInMillis = localMillis }
        val c = Calendar.getInstance(utc)
        c.clear()
        c.set(
            local.get(Calendar.YEAR),
            local.get(Calendar.MONTH),
            local.get(Calendar.DAY_OF_MONTH)
        )
        return c.timeInMillis
    }

    /** Material3 DatePicker 返回的 UTC 毫秒 -> 本地当日零点。 */
    fun fromPickerMillis(pickerMillis: Long): Long {
        val c = Calendar.getInstance(utc).apply { timeInMillis = pickerMillis }
        val local = Calendar.getInstance()
        local.clear()
        local.set(
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        return local.timeInMillis
    }
}
