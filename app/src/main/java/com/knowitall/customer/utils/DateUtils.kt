package com.knowitall.customer.utils

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS


    fun getAge(dob: Calendar): Int {
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age + 1
    }

    fun formatTimestampForOrder(timestamp: String): String {
        val format = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH
        )
        format.timeZone = TimeZone.getTimeZone("UTC")
        var time: Long
        try {
            time = format.parse(timestamp)!!.time
        } catch (e: Exception) {
            return ""
        }
        val date = Date()
        date.time = time
        return SimpleDateFormat("HH:MM a dd MMM yyyy", Locale.ENGLISH).format(date)
    }

    fun formatTimestamp(timestamp: String): String {
        val format = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH
        )
        format.timeZone = TimeZone.getTimeZone("UTC")
        var time: Long
        try {
            time = format.parse(timestamp)!!.time
        } catch (e: Exception) {
            return ""
        }

        val now = System.currentTimeMillis()

        if (time < 1000000000000L) {
            time *= 1000
        }

        if (time > now || time <= 0) {
            return ""
        }

        val diff: Long = now - time
        return when {
            (diff < MINUTE_MILLIS) -> "Just now"
            (diff < 60 * MINUTE_MILLIS) -> (diff / MINUTE_MILLIS).toString() + " m"
            (diff < 24 * HOUR_MILLIS) -> (diff / HOUR_MILLIS).toString() + " h"
            (diff < 7 * DAY_MILLIS) -> (diff / DAY_MILLIS).toString() + " d"
            (diff <= 21 * DAY_MILLIS) -> (diff / DAY_MILLIS / 7).toString() + " w"
            (diff > 21 * DAY_MILLIS) -> {
                val date = Date()
                date.time = time
                return SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH).format(date)
            }
            else -> ""
        }
    }

    fun formatTimestampForPost(timestamp: String): String {
        val format = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH
        )
        format.timeZone = TimeZone.getTimeZone("UTC")
        var time: Long
        try {
            time = format.parse(timestamp)!!.time
        } catch (e: Exception) {
            return ""
        }
        val date = Date()
        date.time = time
        return SimpleDateFormat("MMM dd HH:MM", Locale.ENGLISH).format(date)
    }

    fun formatTimestampDate(timestamp: String): String {
        val format = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH
        )
        format.timeZone = TimeZone.getTimeZone("UTC")
        var time: Long
        try {
            time = format.parse(timestamp)!!.time
        } catch (e: Exception) {
            return ""
        }

        val now = System.currentTimeMillis()

        if (time < 1000000000000L) {
            time *= 1000
        }

        if (time > now || time <= 0) {
            return ""
        }

        val date = Date()
        date.time = time
        return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date)
    }
}