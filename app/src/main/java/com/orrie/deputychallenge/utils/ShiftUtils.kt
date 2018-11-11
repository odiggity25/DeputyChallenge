package com.orrie.deputychallenge.utils

import com.orrie.deputychallenge.models.ShiftChange
import io.reactivex.Single

class ShiftUtils(
    private val locationManager: LocationManager,
    private val dateUtils: DateUtils
) {

    fun buildShiftChangeFromCurrentLocationAndTime(): Single<ShiftChange> {
        return locationManager.getLocation()
            .map { location ->
                val currentDate = dateUtils.getCurrentDateString()
                ShiftChange(currentDate, location.latitude.toString(), location.longitude.toString())
            }
    }
}