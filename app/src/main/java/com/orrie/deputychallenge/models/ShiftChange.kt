package com.orrie.deputychallenge.models

/**
 * Whenever we want to start or end a shift, this is the format
 * the api expects
 */
data class ShiftChange(
    val time: String,
    val latitude: String,
    val longitude: String
)