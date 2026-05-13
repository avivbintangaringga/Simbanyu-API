package com.avivba.simbanyu.api.utils

import org.springframework.security.core.Authentication
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun Instant.getMonthName(): String {
    // TODO: Use a better way to get the month name
    val idLocale = Locale.forLanguageTag("id-ID")
    val formatter = DateTimeFormatter.ofPattern("MMMM", idLocale).withZone(ZoneId.systemDefault())
    return formatter.format(this)
}

fun Instant.getYear(): Int {
    return this.atZone(ZoneId.systemDefault()).year
}

fun Authentication.hasRole(role: String): Boolean {
    return authorities.any { it.authority == "ROLE_$role" }
}

fun Double.toDottedCurrency(prefix: String = "Rp "): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = '.'
    }
    val formatter = DecimalFormat("#,###", symbols)
    return prefix + formatter.format(this)
}
