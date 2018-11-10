package com.orrie.deputychallenge.models

data class Shift(
    val id: Int,
    val start: String,
    val end: String?,
    val startLatitude: Float,
    val startLongitude: Float,
    val endLatitude: Float?,
    val endLongitude: Float?,
    val image: String
)