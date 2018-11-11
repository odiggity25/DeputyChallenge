package com.orrie.deputychallenge.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.Single
import timber.log.Timber


class LocationManager(context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * This will attempt to get the device's location emitting a [SecurityException] if the user has
     * not already granted location permission
     */
    @SuppressLint("MissingPermission")
    fun getLocation(): Single<Location> {
        return Single.create { emitter ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location == null) {
                        emitter.onSafeError(Exception("Location was null"))
                    } else {
                        emitter.onSuccess(location)
                    }
                }
                .addOnFailureListener {
                    Timber.e(it, "Failed to get user's location")
                    emitter.onSafeError(it)
                }
        }
    }
}