package com.avivba.simbanyu.api.utils

import org.apache.tika.Tika

val tika = Tika()

fun detectContentType(file: ByteArray): String {
    return tika.detect(file)
}