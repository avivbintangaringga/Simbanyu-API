package com.avivba.simbanyu.api.utils

import java.security.MessageDigest

fun sha512(value: String): String {
    return MessageDigest
        .getInstance("SHA-512")
        .digest(value.toByteArray())
        .joinToString("") {
            "%02x".format(it)
        }
}