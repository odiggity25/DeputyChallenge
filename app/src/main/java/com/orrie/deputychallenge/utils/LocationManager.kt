package com.orrie.deputychallenge.utils

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.Single
import timber.log.Timber
import java.lang.Exception


class LocationManager(context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

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