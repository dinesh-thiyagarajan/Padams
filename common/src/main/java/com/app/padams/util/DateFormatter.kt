package com.app.padams.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val fullDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    fun formatDateLabel(timestamp: Long): String {
        if (timestamp == 0L) return "Unknown date"

        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        return when {
            isSameDay(now, date) -> "Today"
            isYesterday(now, date) -> "Yesterday"
            isSameYear(now, date) -> {
                SimpleDateFormat("MMMM d", Locale.getDefault()).format(Date(timestamp))
            }
            else -> fullDateFormat.format(Date(timestamp))
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(now: Calendar, date: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = now.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(yesterday, date)
    }

    private fun isSameYear(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }
}
