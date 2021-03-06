package com.orrie.deputychallenge.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())

    fun getCurrentDateStringInIso8601(): String {
        return dateFormat.format(Date())
    }
}