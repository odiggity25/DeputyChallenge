package com.orrie.deputychallenge.utils

import android.content.Context
import android.util.DisplayMetrics

fun Int.dpToPx(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return Math.round(this.toFloat() * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
}