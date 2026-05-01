package com.avivba.simbanyu.api.utils

import org.springframework.security.core.Authentication
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Instant.getMonthName(): String {
    // TODO: Use a better way to get the month name
    val idLocale = Locale.forLanguageTag("id-ID")
    val jakartaZone = ZoneId.of("Asia/Jakarta")
    val formatter = DateTimeFormatter.ofPattern("MMMM", idLocale).withZone(jakartaZone)
    return formatter.format(this)
}

fun Authentication.hasRole(role: String): Boolean {
    return authorities.any { it.authority == "ROLE_$role" }
}