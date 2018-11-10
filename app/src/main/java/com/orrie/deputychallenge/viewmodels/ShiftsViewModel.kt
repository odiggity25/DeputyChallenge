package com.orrie.deputychallenge.viewmodels

import com.orrie.deputychallenge.models.Shift
import com.orrie.deputychallenge.models.ShiftChange
import com.orrie.deputychallenge.repositories.ShiftsRepository
import com.orrie.deputychallenge.utils.LocationManager
import com.orrie.deputychallenge.utils.autoDispose
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class ShiftsViewModel(
    private val shiftsRepository: ShiftsRepository,
    private val locationManager: LocationManager,
    private val exits: Observable<Unit>
) {

    private val requestLocationPermissionShowsSubject = PublishSubject.create<Unit>()
    val requestLocationPermissionShows: Observable<Unit> = requestLocationPermissionShowsSubject.hide()

    fun addShiftClicked() {
        locationManager.getLocation()
            .map { location ->
                ShiftChange("May 10, 1988", location.latitude.toString(), location.longitude.toString())
            }
            .flatMapCompletable { shiftsRepository.startShift(it) }
            .subscribe({
                Timber.d("Successfully started shift")
            }, {
                Timber.e(it, "Failed to start shift")
                if (it is SecurityException) {
                    requestLocationPermissionShowsSubject.onNext(Unit)
                }
            }).autoDispose(exits)
    }

    private val shiftsUpdatesSubject = BehaviorSubject.create<List<Shift>>()
    val shiftsUpdates = shiftsUpdatesSubject.hide()

    init {
        shiftsRepository.getShifts()
            .subscribe({     shifts ->
                Timber.d("Shifts are $shifts")
                shiftsUpdatesSubject.onNext(shifts)
            }, {
                Timber.e(it, "Failed to retrieve shifts")
            }).autoDispose(exits)


        val shift1 = Shift(1, "May 3, 2018", "May 4, 2018", 23.00F, 24.00F, 23.00F, 24.00F, "https://unsplash.it/500/500/?random")
        val shift2 = Shift(1, "May 5, 2018", "May 5, 2018", 23.00F, 24.00F, 23.00F, 24.00F, "https://unsplash.it/500/500/?random")

        shiftsUpdatesSubject.onNext(listOf(shift1, shift2))
    }
}