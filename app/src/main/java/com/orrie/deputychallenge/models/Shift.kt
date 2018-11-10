package com.orrie.deputychallenge.models

import java.util.*

data class Shift(
    val id: Int,
    val start: Date,
    val end: Date?,
    val startLatitude: Float,
    val startLongitude: Float,
    val endLatitude: Float?,
    val endLongitude: Float?,
    val image: String
)