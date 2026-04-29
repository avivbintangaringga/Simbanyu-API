package com.avivba.simbanyu.api.utils

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Instant.getMonthName(): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM", Locale.forLanguageTag("id-ID"))
    return formatter.format(this)
}