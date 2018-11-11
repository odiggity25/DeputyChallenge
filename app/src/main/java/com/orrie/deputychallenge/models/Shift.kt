package com.orrie.deputychallenge.models

/**
 * This represents a shift that is either in progress
 * or completed
 */
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